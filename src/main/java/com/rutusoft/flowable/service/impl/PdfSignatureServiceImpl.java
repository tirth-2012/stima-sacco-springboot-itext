package com.rutusoft.flowable.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import com.rutusoft.flowable.service.PdfSignatureService;
import org.springframework.beans.factory.annotation.Value;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.geom.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;


@Service
public class PdfSignatureServiceImpl
        implements PdfSignatureService {

    @Value("${signature.p12-path}")
    private String p12Path;

    @Value("${signature.p12-password}")
    private String password;

    @Value("${signature.reason}")
    private String reason;

    @Value("${signature.location}")
    private String location;

    @Override
    public byte[] signPdf(byte[] pdfBytes) {

        try {

            Security.addProvider(
                    new BouncyCastleProvider()
            );

            KeyStore ks =
                    KeyStore.getInstance("PKCS12");

            InputStream is =
                    new ClassPathResource(p12Path)
                            .getInputStream();

            ks.load(is, password.toCharArray());

            String alias =
                    ks.aliases().nextElement();

            PrivateKey pk =
                    (PrivateKey) ks.getKey(
                            alias,
                            password.toCharArray()
                    );

            Certificate[] chain =
                    ks.getCertificateChain(alias);

            PdfDocument tempDoc =
                    new PdfDocument(
                            new PdfReader(
                                    new ByteArrayInputStream(pdfBytes)
                            )
                    );

            int lastPage =
                    tempDoc.getNumberOfPages();

            tempDoc.close();

            ByteArrayOutputStream signedPdf =
                    new ByteArrayOutputStream();

            PdfSigner signer =
                    new PdfSigner(
                            new PdfReader(
                                    new ByteArrayInputStream(pdfBytes)
                            ),
                            signedPdf,
                            new StampingProperties()
                    );

            Rectangle rect =
                    new Rectangle(
                            36,     // left margin
                            36,     // bottom margin
                            250,    // width
                            100     // height
                    );

            PdfSignatureAppearance appearance =
                    signer.getSignatureAppearance();

            appearance
                    .setReason(reason)
                    .setLocation(location)
                    .setPageRect(rect)
                    .setPageNumber(lastPage)
                    .setReuseAppearance(false);

            appearance.setLayer2Text(
                    "Digitally Signed By Stima SACCO\n" +
                            "Reason: " + reason + "\n" +
                            "Location: " + location + "\n" +
                            "Date: " +
                            java.time.LocalDateTime.now()
            );

            signer.setFieldName(
                    "companySignature_" +
                            System.currentTimeMillis()
            );

            IExternalSignature signature =
                    new PrivateKeySignature(
                            pk,
                            DigestAlgorithms.SHA256,
                            BouncyCastleProvider.PROVIDER_NAME
                    );

            IExternalDigest digest =
                    new BouncyCastleDigest();

            signer.signDetached(
                    digest,
                    signature,
                    chain,
                    null,
                    null,
                    null,
                    0,
                    PdfSigner.CryptoStandard.CMS
            );

            return signedPdf.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException(
                    "PDF signing failed",
                    ex
            );
        }
    }
}