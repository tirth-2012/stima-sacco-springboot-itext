package com.rutusoft.flowable;

import com.rutusoft.flowable.dto.*;
import com.rutusoft.flowable.entity.Product;
import com.rutusoft.flowable.enums.Branch;
import com.rutusoft.flowable.enums.Group;
import com.rutusoft.flowable.enums.Region;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@EnableJpaAuditing

@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
public class SpringbootFlowableServiceApplication {
	private final RuntimeService runtimeService;
	private final GroupManagementService groupManagementService;
	private final UserManagementService userManagementService;
	private final ProductService productService;
	private final CustomerService customerService;
	private final SectorService sectorService;
	private final SubsectorService subsectorService;
	private final CategoryService categoryService;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootFlowableServiceApplication.class, args);
	}

	@PostConstruct
	private void verifyFlowableConfiguration() {
		initUserConfiguration();
		iniProductConfiguration();
		initCustomerConfiguration();
		initSectors();
		initSubSectors();
		initCategories();

	}

	private void initUserConfiguration() {
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().active().list();
		log.info("processInstances size logger : {}", processInstances.size());
		//Production groups setup
		createGroup(Group.MEMBER.getCode(), Group.MEMBER.getDisplayName());
		createGroup(Group.BRANCH_MANAGER.getCode(), Group.BRANCH_MANAGER.getDisplayName());
		createGroup(Group.CREDIT_OFFICER.getCode(), Group.CREDIT_OFFICER.getDisplayName());
		createGroup(Group.GUARANTOR_VERIFICARION_OFFICER.getCode(), Group.GUARANTOR_VERIFICARION_OFFICER.getDisplayName());
		createGroup(Group.SYSTEM_ADMINISTRATOR.getCode(), Group.SYSTEM_ADMINISTRATOR.getDisplayName());
		createGroup(Group.CREDIT_APRAISAL.getCode(), Group.CREDIT_APRAISAL.getDisplayName());
		createGroup(Group.SENIOR_CREDIT_MANAGER.getCode(), Group.SENIOR_CREDIT_MANAGER.getDisplayName());
		createGroup(Group.CREDIT_COMMITTEE.getCode(), Group.CREDIT_COMMITTEE.getDisplayName());
		createGroup(Group.BRANCH_CREDIT_COMMITTEE.getCode(), Group.BRANCH_CREDIT_COMMITTEE.getDisplayName());
		createGroup(Group.LEGAL_OFFICER.getCode(), Group.LEGAL_OFFICER.getDisplayName());
		createGroup(Group.CREDIT_ADMINISTRATOR.getCode(), Group.CREDIT_ADMINISTRATOR.getDisplayName());

		//Production users setup

		//Groups for BDO stage
		createUser("sacca.admin", 	"Admin", 	"", 	"sg.vadaviya@gmail.com", 	"DIB@2026", Group.SYSTEM_ADMINISTRATOR.getCode(),  Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		//Groups for Branch Manager stage
		createUser("pwanjiku", 	"Patricia","Wanjiku", 	"sg.vadaviya@gmail.com", 	"DIB@2026", Group.BRANCH_MANAGER.getCode(), Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("sndungu", 	"Samuel", 	"Ndung'u", 	"sg.vadaviya@gmail.com", 	"DIB@2026", Group.BRANCH_MANAGER.getCode(), Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		//Groups for Risk Credit Analyst stage
		createUser("akapoor", 	"Ayesha", 	"Kapoor", 		"sg.vadaviya@gmail.com", 	"DIB@2026", Group.CREDIT_APRAISAL.getCode(), Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("jmwangi", 	"James", 	"Mwangi", 		"sg.vadaviya@gmail.com", 	"DIB@2026", Group.CREDIT_APRAISAL.getCode(), Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		//Groups for Approval stages
		createUser("kmutua", 	"Kevin", 	"Mutua", 		"sg.vadaviya@gmail.com", 		"DIB@2026", Group.CREDIT_OFFICER.getCode(),  Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		createUser("gkristan", 	"Gary", 	"Christan", 		"sg.vadaviya@gmail.com", 		"DIB@2026", Group.GUARANTOR_VERIFICARION_OFFICER.getCode(),  Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		createUser("fochieng", 	"Fatuma", 	"Ochieng", 	"sg.vadaviya@gmail.com", 	"DIB@2026", Group.SENIOR_CREDIT_MANAGER.getCode(),  		Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("pomondi", 	"Paul", 	"Omondi", 		"sg.vadaviya@gmail.com", 	"DIB@2026", Group.SENIOR_CREDIT_MANAGER.getCode(),    		Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		createUser("rnjeri", 	"Ruth", 	"Njeri", 		"sg.vadaviya@gmail.com", 		"DIB@2026", Group.CREDIT_COMMITTEE.getCode(),  			Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("ckimani", 	"Charles", "Kimani", 		"sg.vadaviya@gmail.com", 	"DIB@2026", Group.CREDIT_COMMITTEE.getCode(),  			Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("jakinyi", 	"Joyce", "Akinyi", 		"sg.vadaviya@gmail.com", 	"DIB@2026", Group.BRANCH_CREDIT_COMMITTEE.getCode(), Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		//Groups for Legal stage
		createUser("mokeyo", 	"Michael", "Okeyo", 		"sg.vadaviya@gmail.com", 		"DIB@2026", Group.LEGAL_OFFICER.getCode(),  				Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("gkamau", 	"Grace", 	"Kamau", 		"sg.vadaviya@gmail.com", 		"DIB@2026", Group.LEGAL_OFFICER.getCode(),  				Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		//Groups for Disbursement stage
		createUser("sochieng", 	"Sandra", 	"Ochieng", 	"sg.vadaviya@gmail.com", 	"DIB@2026", Group.CREDIT_ADMINISTRATOR.getCode(),  		Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		createUser("pkariuki", 	"Peter", 	"Kariuki", 	"sg.vadaviya@gmail.com", 	"DIB@2026", Group.CREDIT_ADMINISTRATOR.getCode(),  		Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());

		List<CustomerResponseDto> allCustomers = customerService.getAllCustomers(0, 50).stream().toList();
		log.info("allCustomers size : {}", allCustomers.size());
		for(CustomerResponseDto customerResponseDto : allCustomers) {
			log.info("Creating customer : {}", customerResponseDto.getFullName());
			createUser(customerResponseDto.getCifNumber(), customerResponseDto.getFullName(), "", customerResponseDto.getEmail(), "DIB@2026", Group.MEMBER.getCode(), Region.CENTRAL.getCode(), Branch.BRANCH_HILLS.getCode());
		}
	}

	private void createGroup(String groupId, String name) {
		GroupDto groupDto = new GroupDto();
		groupDto.setGroupId(groupId);
		groupDto.setName(name);
		groupManagementService.createNewGroup(groupDto);
	}

	private void createUser(String userId, String firstName, String lastName, String emailId, String password, String groupCode, String regionCode, String branchCode) {
		CreateUserDto userDto =  new CreateUserDto();
		userDto.setUserId(userId);
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		userDto.setEmail(emailId);
		userDto.setPassword(password);
		userDto.setBranchCode(branchCode);
		userDto.setRegionCode(regionCode);
		List<String> groups = new ArrayList<>();
		groups.add(groupCode);
		userDto.setGroups(new ArrayList<>(groups));
		userManagementService.createUser(userDto);
	}

	private void iniProductConfiguration() {

		// SACCO Products
		ProductRequestDto product1 = new ProductRequestDto();
		product1.setProductName("Development Loan");
		product1.setDescription("General development financing for members in active payroll deduction.");
		product1.setCategory("SACCO");
		product1.setProductType(null);
		product1.setRateType(13.0);
		product1.setProductCode("DEV");

		createProduct(product1);

		ProductRequestDto product2 = new ProductRequestDto();
		product2.setProductName("Emergency Loan");
		product2.setDescription("Short-term cover for medical, bereavement and urgent needs.");
		product2.setCategory("SACCO");
		product2.setProductType(null);
		product2.setRateType(14.0);
		product2.setProductCode("EMG");

		createProduct(product2);

		ProductRequestDto product3 = new ProductRequestDto();
		product3.setProductName("School Fees Loan");
		product3.setDescription("Term-aligned facility for school and tertiary education fees.");
		product3.setCategory("SACCO");
		product3.setProductType(null);
		product3.setRateType(13.5);
		product3.setProductCode("SCH");

		createProduct(product3);

		ProductRequestDto product4 = new ProductRequestDto();
		product4.setProductName("Salary Advance");
		product4.setDescription("Pay-day bridge limited to one month net pay.");
		product4.setCategory("SACCO");
		product4.setProductType(null);
		product4.setRateType(9.0);
		product4.setProductCode("SAL");

		createProduct(product4);

		ProductRequestDto product5 = new ProductRequestDto();
		product5.setProductName("Mobile Loan");
		product5.setDescription("Instant low-value facility disbursed via mobile wallet.");
		product5.setCategory("SACCO");
		product5.setProductType(null);
		product5.setRateType(11.0);
		product5.setProductCode("MOB");

		createProduct(product5);

		ProductRequestDto product6 = new ProductRequestDto();
		product6.setProductName("Guarantor-backed Loan");
		product6.setDescription("For members without payroll, fully secured by member guarantors.");
		product6.setCategory("SACCO");
		product6.setProductType(null);
		product6.setRateType(14.0);
		product6.setProductCode("GTR");

		createProduct(product6);

		ProductRequestDto product7 = new ProductRequestDto();
		product7.setProductName("Asset Finance");
		product7.setDescription("Motor vehicle and asset financing secured by logbook or title.");
		product7.setCategory("SACCO");
		product7.setProductType(null);
		product7.setRateType(15.0);
		product7.setProductCode("ASF");

		createProduct(product7);

		ProductRequestDto product8 = new ProductRequestDto();
		product8.setProductName("Top-up Loan");
		product8.setDescription("Additional facility on top of a performing Development Loan.");
		product8.setCategory("SACCO");
		product8.setProductType(null);
		product8.setRateType(13.5);
		product8.setProductCode("TOP");

		createProduct(product8);
	}

	private void createProduct(ProductRequestDto product) {
		try {
			productService.createProduct(product);
		} catch (ValidationException e) {
			log.error("Validation error : {}", e.getMessage());
		}

	}

	private void initCustomerConfiguration() {

		// 🔹 Customer 1
		CustomerRequestDto c1 = new CustomerRequestDto();
		c1.setFullName("Faith Nyambura Wairimu");
		c1.setGender("Male");
		c1.setDateOfBirth(LocalDate.of(1990, 1, 1));
		c1.setNationalId("NID2001");
		c1.setKraPin("KRA2001");
		c1.setEmail("sg.vadaviya@gmail.com");
		c1.setMobileNumber("254710000001");
		c1.setPhysicalAddress("Nairobi Industrial Area");
		c1.setPostalAddress("P.O Box 1001-00100 Nairobi");
		c1.setNationality("Kenyan");
		c1.setMaritalStatus("Married");
		c1.setCifNumber("STM-00038111");
		c1.setCustomerType("Corporate - SME");
		c1.setAccountSince("March 2019");
		c1.setExistingCustomer(true);
		c1.setKycVerified(true);
		c1.setStatus("ACTIVE");
		c1.setIntakeChannel("Branch - Nairobi HQ");
		c1.setRelationshipManager("akipchoge");
		c1.setExistingFacilities(2);
		c1.setTotalExposure("KES 4.2M");
		c1.setRepaymentRecord("Excellent");
		c1.setLastFacility("Murabaha");
		c1.setBankName("Co-operative Bank Kenya");
		c1.setAccountNumber("01129847595");
		c1.setBranchName("Westlands Branch");
		c1.setAccountType("Current");
		c1.setSwiftCode("KCOOKENA");
		c1.setLoanAmountLimit(5000000.0);
		c1.setAvailableLoanLimit(5000000.0);

		List<CustomerObligationRequestDto> obligations1 = new ArrayList<>();

		CustomerObligationRequestDto o11 = new CustomerObligationRequestDto();
		o11.setCifNumber("STM-00038111");
		o11.setLender("DIB BANK");
		o11.setFacilityType("Murabaha");
		o11.setOutstanding(1200000.0);
		o11.setMonthlyCommitment(45000.0);
		o11.setSource("system");
		o11.setStatus("active");

		CustomerObligationRequestDto o12 = new CustomerObligationRequestDto();
		o12.setCifNumber("STM-00038111");
		o12.setLender("KCB BANK");
		o12.setFacilityType("Working Capital");
		o12.setOutstanding(800000.0);
		o12.setMonthlyCommitment(30000.0);
		o12.setSource("system");
		o12.setStatus("active");

		obligations1.add(o11);
		obligations1.add(o12);

		c1.setObligations(obligations1);

		createCustomer(c1);


		// 🔹 Customer 2
		CustomerRequestDto c2 = new CustomerRequestDto();
		c2.setFullName("Samuel Mutua Nzioka");
		c2.setGender("Male");
		c2.setDateOfBirth(LocalDate.of(1985, 6, 15));
		c2.setNationalId("NID2002");
		c2.setKraPin("KRA2002");
		c2.setEmail("sg.vadaviya@gmail.com");
		c2.setMobileNumber("254710000002");
		c2.setPhysicalAddress("Mombasa Road, Nairobi");
		c2.setPostalAddress("P.O Box 2002-00100 Nairobi");
		c2.setNationality("Kenyan");
		c2.setMaritalStatus("Married");
		c2.setCifNumber("STM-00038112");
		c2.setCustomerType("Corporate");
		c2.setAccountSince("June 2018");
		c2.setExistingCustomer(true);
		c2.setKycVerified(true);
		c2.setStatus("ACTIVE");
		c2.setIntakeChannel("Branch - Mombasa");
		c2.setRelationshipManager("akipchoge");
		c2.setExistingFacilities(3);
		c2.setTotalExposure("KES 6.8M");
		c2.setRepaymentRecord("Excellent");
		c2.setLastFacility("Ijara");
		c2.setBankName("Co-operative Bank Kenya");
		c2.setAccountNumber("01129847596");
		c2.setBranchName("Westlands Branch");
		c2.setAccountType("Current");
		c2.setSwiftCode("KCOOKENA");
		c2.setLoanAmountLimit(5000000.0);
		c2.setAvailableLoanLimit(5000000.0);

		List<CustomerObligationRequestDto> obligations2 = new ArrayList<>();

		CustomerObligationRequestDto o21 = new CustomerObligationRequestDto();
		o21.setCifNumber("STM-00038112");
		o21.setLender("EQUITY BANK");
		o21.setFacilityType("Ijara");
		o21.setOutstanding(2500000.0);
		o21.setMonthlyCommitment(90000.0);
		o21.setSource("external");
		o21.setStatus("active");

		CustomerObligationRequestDto o22 = new CustomerObligationRequestDto();
		o22.setCifNumber("STM-00038112");
		o22.setLender("ABSA BANK");
		o22.setFacilityType("Overdraft");
		o22.setOutstanding(600000.0);
		o22.setMonthlyCommitment(25000.0);
		o22.setSource("external");
		o22.setStatus("active");

		obligations2.add(o21);
		obligations2.add(o22);

		c2.setObligations(obligations2);

		createCustomer(c2);


		// 🔹 Customer 3
		CustomerRequestDto c3 = new CustomerRequestDto();
		c3.setFullName("Mary Akinyi Onyango");
		c3.setGender("Female");
		c3.setDateOfBirth(LocalDate.of(1992, 3, 10));
		c3.setNationalId("NID2003");
		c3.setKraPin("KRA2003");
		c3.setEmail("sg.vadaviya@gmail.com");
		c3.setMobileNumber("254710000003");
		c3.setPhysicalAddress("Kisumu Industrial Park");
		c3.setPostalAddress("P.O Box 3003-40100 Kisumu");
		c3.setNationality("Kenyan");
		c3.setMaritalStatus("Unmarried");
		c3.setCifNumber("STM-00038113");
		c3.setCustomerType("Corporate");
		c3.setAccountSince("January 2020");
		c3.setExistingCustomer(true);
		c3.setKycVerified(true);
		c3.setStatus("ACTIVE");
		c3.setIntakeChannel("Online");
		c3.setRelationshipManager("akipchoge");
		c3.setExistingFacilities(1);
		c3.setTotalExposure("KES 2.5M");
		c3.setRepaymentRecord("Excellent");
		c3.setLastFacility("Wakala");
		c3.setBankName("Co-operative Bank Kenya");
		c3.setAccountNumber("01129847597");
		c3.setBranchName("Westlands Branch");
		c3.setAccountType("Current");
		c3.setSwiftCode("KCOOKENA");
		c3.setLoanAmountLimit(50000.0);
		c3.setAvailableLoanLimit(50000.0);

		List<CustomerObligationRequestDto> obligations3 = new ArrayList<>();

		CustomerObligationRequestDto o31 = new CustomerObligationRequestDto();
		o31.setCifNumber("STM-00038113");
		o31.setLender("NCBA BANK");
		o31.setFacilityType("Wakala");
		o31.setOutstanding(900000.0);
		o31.setMonthlyCommitment(35000.0);
		o31.setSource("system");
		o31.setStatus("active");

		CustomerObligationRequestDto o32 = new CustomerObligationRequestDto();
		o32.setCifNumber("STM-00038113");
		o32.setLender("DTB BANK");
		o32.setFacilityType("Trade Finance");
		o32.setOutstanding(700000.0);
		o32.setMonthlyCommitment(28000.0);
		o32.setSource("system");
		o32.setStatus("active");

		obligations3.add(o31);
		obligations3.add(o32);

		c3.setObligations(obligations3);

		createCustomer(c3);


		// 🔹 Customer 4
		CustomerRequestDto c4 = new CustomerRequestDto();
		c4.setFullName("Brian Otieno Wafula");
		c4.setGender("Male");
		c4.setDateOfBirth(LocalDate.of(1988, 7, 20));
		c4.setNationalId("NID2004");
		c4.setKraPin("KRA2004");
		c4.setEmail("sg.vadaviya@gmail.com");
		c4.setMobileNumber("254710000004");
		c4.setPhysicalAddress("Nakuru Transport Yard");
		c4.setPostalAddress("P.O Box 4004-20100 Nakuru");
		c4.setNationality("Kenyan");
		c4.setMaritalStatus("Married");
		c4.setCifNumber("STM-00038114");
		c4.setCustomerType("Corporate");
		c4.setAccountSince("January 2017");
		c4.setExistingCustomer(true);
		c4.setKycVerified(true);
		c4.setStatus("ACTIVE");
		c4.setIntakeChannel("Branch - Nakuru");
		c4.setRelationshipManager("akipchoge");
		c4.setExistingFacilities(4);
		c4.setTotalExposure("KES 9.5M");
		c4.setRepaymentRecord("Excellent");
		c4.setLastFacility("Diminishing Musharaka");
		c4.setBankName("Co-operative Bank Kenya");
		c4.setAccountNumber("01129847598");
		c4.setBranchName("Westlands Branch");
		c4.setAccountType("Current");
		c4.setSwiftCode("KCOOKENA");
		c4.setLoanAmountLimit(5000000.0);
		c4.setAvailableLoanLimit(5000000.0);

		List<CustomerObligationRequestDto> obligations4 = new ArrayList<>();

		CustomerObligationRequestDto o41 = new CustomerObligationRequestDto();
		o41.setCifNumber("STM-00038114");
		o41.setLender("STANDARD CHARTERED");
		o41.setFacilityType("Diminishing Musharaka");
		o41.setOutstanding(3500000.0);
		o41.setMonthlyCommitment(120000.0);
		o41.setSource("external");
		o41.setStatus("active");

		CustomerObligationRequestDto o42 = new CustomerObligationRequestDto();
		o42.setCifNumber("STM-00038114");
		o42.setLender("KCB BANK");
		o42.setFacilityType("Fleet Financing");
		o42.setOutstanding(1800000.0);
		o42.setMonthlyCommitment(70000.0);
		o42.setSource("external");
		o42.setStatus("active");

		obligations4.add(o41);
		obligations4.add(o42);

		c4.setObligations(obligations4);

		createCustomer(c4);


		// 🔹 Customer 5
		CustomerRequestDto c5 = new CustomerRequestDto();
		c5.setFullName("Joseph Mwangi Kariuki");
		c5.setGender("Female");
		c5.setDateOfBirth(LocalDate.of(1991, 11, 25));
		c5.setNationalId("NID2005");
		c5.setKraPin("KRA2005");
		c5.setEmail("sg.vadaviya@gmail.com");
		c5.setMobileNumber("254710000005");
		c5.setPhysicalAddress("Eldoret CBD");
		c5.setPostalAddress("P.O Box 5005-30100 Eldoret");
		c5.setNationality("Kenyan");
		c5.setMaritalStatus("Unmarried");
		c5.setCifNumber("STM-00038115");
		c5.setCustomerType("SME");
		c5.setAccountSince("August 2021");
		c5.setExistingCustomer(true);
		c5.setKycVerified(true);
		c5.setStatus("ACTIVE");
		c5.setIntakeChannel("Agent");
		c5.setRelationshipManager("akipchoge");
		c5.setExistingFacilities(2);
		c5.setTotalExposure("KES 3.7M");
		c5.setRepaymentRecord("Excellent");
		c5.setLastFacility("Bai Muajjal");
		c5.setBankName("Co-operative Bank Kenya");
		c5.setAccountNumber("01129847599");
		c5.setBranchName("Westlands Branch");
		c5.setAccountType("Current");
		c5.setSwiftCode("KCOOKENA");
		c5.setLoanAmountLimit(5000000.0);
		c5.setAvailableLoanLimit(5000000.0);

		List<CustomerObligationRequestDto> obligations5 = new ArrayList<>();

		CustomerObligationRequestDto o51 = new CustomerObligationRequestDto();
		o51.setCifNumber("STM-00038115");
		o51.setLender("DIB BANK");
		o51.setFacilityType("Bai Muajjal");
		o51.setOutstanding(1100000.0);
		o51.setMonthlyCommitment(50000.0);
		o51.setSource("system");
		o51.setStatus("active");

		CustomerObligationRequestDto o52 = new CustomerObligationRequestDto();
		o52.setCifNumber("STM-00038115");
		o52.setLender("EQUITY BANK");
		o52.setFacilityType("SME Loan");
		o52.setOutstanding(900000.0);
		o52.setMonthlyCommitment(40000.0);
		o52.setSource("system");
		o52.setStatus("active");

		obligations5.add(o51);
		obligations5.add(o52);

		c5.setObligations(obligations5);

		createCustomer(c5);

		// 🔹 Customer 6
		CustomerRequestDto c6 = new CustomerRequestDto();
		c6.setFullName("Catherine Wanja Njoki");
		c6.setGender("Female");
		c6.setDateOfBirth(LocalDate.of(1991, 11, 25));
		c6.setNationalId("NID2006");
		c6.setKraPin("KRA2005");
		c6.setEmail("sg.vadaviya@gmail.com");
		c6.setMobileNumber("254710000006");
		c6.setPhysicalAddress("Eldoret CBD");
		c6.setPostalAddress("P.O Box 5005-30100 Eldoret");
		c6.setNationality("Kenyan");
		c6.setMaritalStatus("Unmarried");
		c6.setCifNumber("STM-00038116");
		c6.setCustomerType("SME");
		c6.setAccountSince("August 2021");
		c6.setExistingCustomer(true);
		c6.setKycVerified(true);
		c6.setStatus("ACTIVE");
		c6.setIntakeChannel("Agent");
		c6.setRelationshipManager("akipchoge");
		c6.setExistingFacilities(2);
		c6.setTotalExposure("KES 3.7M");
		c6.setRepaymentRecord("Excellent");
		c6.setLastFacility("Bai Muajjal");
		c6.setBankName("Co-operative Bank Kenya");
		c6.setAccountNumber("01129847600");
		c6.setBranchName("Westlands Branch");
		c6.setAccountType("Current");
		c6.setSwiftCode("KCOOKENA");
		c6.setLoanAmountLimit(5000000.0);
		c6.setAvailableLoanLimit(5000000.0);

		List<CustomerObligationRequestDto> obligations = new ArrayList<>();

		CustomerObligationRequestDto o1 = new CustomerObligationRequestDto();
		o1.setCifNumber("STM-00038116");
		o1.setLender("DIB BANK");
		o1.setFacilityType("Home Loan");
		o1.setOutstanding(1400000.0);
		o1.setMonthlyCommitment(60000.0);
		o1.setSource("data");
		o1.setStatus("active");

		obligations.add(o1);
		c6.setObligations(obligations);
		createCustomer(c6);
	}

	private void createCustomer(CustomerRequestDto customer) {
		try {
			customerService.createCustomer(customer);
		} catch (ValidationException e) {
			log.error("Customer validation error: {}", e.getMessage());
		}
	}

	private void initSectors() {
		List<SectorDto> sectorDtos = new ArrayList<>();

		SectorDto sectorDto1 = createSectorDto("1000", "AGRICULTURE", "");
		SectorDto sectorDto2 = createSectorDto("2000", "TRADE", "");
		SectorDto sectorDto3 = createSectorDto("3000", "MANUFACTURING AND SERVICING INDUSTRIES", "");
		SectorDto sectorDto4 = createSectorDto("4000", "EDUCATION", "");
		SectorDto sectorDto5 = createSectorDto("5000", "HUMAN HEALTH", "");
		SectorDto sectorDto6 = createSectorDto("6000", "LAND AND HOUSING", "");
		SectorDto sectorDto7 = createSectorDto("7000", "FINANCE, INVESTMENTS AND INSURANCE", "");
		SectorDto sectorDto8 = createSectorDto("8000", "CONSUMPTION AND SOCIAL SERVICES", "");

		sectorDtos.add(sectorDto1);
		sectorDtos.add(sectorDto2);
		sectorDtos.add(sectorDto3);
		sectorDtos.add(sectorDto4);
		sectorDtos.add(sectorDto5);
		sectorDtos.add(sectorDto6);
		sectorDtos.add(sectorDto7);
		sectorDtos.add(sectorDto8);

		for(SectorDto  sectorDto: sectorDtos) {
			try {
				sectorService.createSector(sectorDto);
			}
			catch (ValidationException e) {
				log.error("Validation error while creating sectors : {}", e.getMessage());
			}
		}
	}

	private void initSubSectors() {
		List<SectorDto> sectorDtos = sectorService.listAllSectors();
		for(SectorDto sectorDto : sectorDtos) {
			try {
				log.info("Creating sub sectors for sector code : {}", sectorDto.getCode());
				if (sectorDto.getCode().equals("1000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("1100", "Crop Farming", "", 1L);
					SubsectorDto subsectorDto2 = createSubSectorDto("1200", "Animal Production", "", 1L);
					SubsectorDto subsectorDto3 = createSubSectorDto("1300", "Agricultural supporting services", "", 1L);
					SubsectorDto subsectorDto4 = createSubSectorDto("1400", "Agribusiness", "", 1L);
					SubsectorDto subsectorDto5 = createSubSectorDto("1500", "Forestry and Logging", "", 1L);

					createSubSectorSafely(subsectorDto1);
					createSubSectorSafely(subsectorDto2);
					createSubSectorSafely(subsectorDto3);
					createSubSectorSafely(subsectorDto4);
					createSubSectorSafely(subsectorDto5);

				} else if (sectorDto.getCode().equals("2000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("2100", "Wholesale and Retail", "", 2L);
					SubsectorDto subsectorDto2 = createSubSectorDto("2200", "Transport", "", 2L);
					SubsectorDto subsectorDto3 = createSubSectorDto("2300", "Hospitality", "", 2L);
					SubsectorDto subsectorDto4 = createSubSectorDto("2400", "Foreign Trade", "", 2L);

					createSubSectorSafely(subsectorDto1);
					createSubSectorSafely(subsectorDto2);
					createSubSectorSafely(subsectorDto3);
					createSubSectorSafely(subsectorDto4);
				} else if (sectorDto.getCode().equals("3000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("3100", "Cottage Industry", "", 3L);
					SubsectorDto subsectorDto2 = createSubSectorDto("3200", "Servicing Industry", "", 3L);
					SubsectorDto subsectorDto3 = createSubSectorDto("3300", "Information, Communication and Technology", "", 3L);

					createSubSectorSafely(subsectorDto1);
					createSubSectorSafely(subsectorDto2);
					createSubSectorSafely(subsectorDto3);
				} else if (sectorDto.getCode().equals("4000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("4100", "Education and related services", "", 4L);

					createSubSectorSafely(subsectorDto1);
				} else if (sectorDto.getCode().equals("5000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("5100", "Human health and related services", "", 5L);
					createSubSectorSafely(subsectorDto1);
				}
				else if (sectorDto.getCode().equals("6000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("6100", "Land", "", 6L);
					SubsectorDto subsectorDto2 = createSubSectorDto("6200", "Housing", "", 6L);

					createSubSectorSafely(subsectorDto1);
					createSubSectorSafely(subsectorDto2);
				} else if (sectorDto.getCode().equals("7000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("7100", "Microfinance", "", 7L);
					SubsectorDto subsectorDto2 = createSubSectorDto("7200", "Commercial Banks", "", 7L);
					SubsectorDto subsectorDto3 = createSubSectorDto("7300", "Mortgage Finance", "", 7L);
					SubsectorDto subsectorDto4 = createSubSectorDto("7400", "Insurance", "", 7L);
					SubsectorDto subsectorDto5 = createSubSectorDto("7500", "Investments", "", 7L);

					createSubSectorSafely(subsectorDto1);
					createSubSectorSafely(subsectorDto2);
					createSubSectorSafely(subsectorDto3);
					createSubSectorSafely(subsectorDto4);
					createSubSectorSafely(subsectorDto5);
				} else if (sectorDto.getCode().equals("8000")) {
					SubsectorDto subsectorDto1 = createSubSectorDto("8100", "Utilities", "", 8L);
					SubsectorDto subsectorDto2 = createSubSectorDto("8200", "Consumer Durables", "", 8L);
					SubsectorDto subsectorDto3 = createSubSectorDto("8300", " Social and communal expenses", "", 8L);

					createSubSectorSafely(subsectorDto1);
					createSubSectorSafely(subsectorDto2);
					createSubSectorSafely(subsectorDto3);
				}
			} catch (ValidationException validationException) {
				log.info("Error occurred while creating sub section");
			}
		}
	}

	private void initCategories() {
		log.info("Creating categories");
		List<SubsectorDto> subsectorDtos = subsectorService.listAllSubsectors();
		log.info("subsectorDtos : {}", subsectorDtos.size());
		for(SubsectorDto subsectorDto : subsectorDtos) {
			try {
				log.info("Creating categories for sub sector code : {}", subsectorDto.getCode());
				if (subsectorDto.getCode().equals("1100")) {
					CategoryDto subsectorDto1 = createCategoryDto("1110", "Tea", "", 1L);
					CategoryDto subsectorDto2 = createCategoryDto("1120", "Coffee", "", 1L);
					CategoryDto subsectorDto3 = createCategoryDto("1130", "Sugarcane", "", 1L);
					CategoryDto subsectorDto4 = createCategoryDto("1140", "Others, cotton, sisal etc", "", 1L);
					CategoryDto subsectorDto5 = createCategoryDto("1150", "Cereals such as maize, wheat, sorghum. Millet etc", "", 1L);
					CategoryDto subsectorDto6 = createCategoryDto("1160", "Legumes such as beans, peas, snow peas, cow peas, french beans etc", "", 1L);
					CategoryDto subsectorDto7 = createCategoryDto("1170", "Horticulture crops such as vegetables, fruits, flowers", "", 1L);
					CategoryDto subsectorDto8 = createCategoryDto("1180", "Roots & tubers such as Irish potatoes, sweet potatoes and cassava", "", 1L);
					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
					createCategorySafely(subsectorDto4);
					createCategorySafely(subsectorDto5);
					createCategorySafely(subsectorDto6);
					createCategorySafely(subsectorDto7);
					createCategorySafely(subsectorDto8);
				}
				else if (subsectorDto.getCode().equals("1200")) {
					CategoryDto subsectorDto1 = createCategoryDto("1210", "Dairy farming", "", 2L);
					CategoryDto subsectorDto2 = createCategoryDto("1220", "Beef Production", "", 2L);
					CategoryDto subsectorDto3 = createCategoryDto("1230", "Poultry Farming", "", 2L);
					CategoryDto subsectorDto4 = createCategoryDto("1240", "Bee keeping", "", 2L);
					CategoryDto subsectorDto5 = createCategoryDto("1250", "Rabbit Farming", "", 2L);
					CategoryDto subsectorDto6 = createCategoryDto("1260", "Sheep and Goat Rearing", "", 2L);
					CategoryDto subsectorDto7 = createCategoryDto("1270", "Pig Farming", "", 2L);
					CategoryDto subsectorDto8 = createCategoryDto("1280", "Others", "", 2L);
					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
					createCategorySafely(subsectorDto4);
					createCategorySafely(subsectorDto5);
					createCategorySafely(subsectorDto6);
					createCategorySafely(subsectorDto7);
					createCategorySafely(subsectorDto8);
				}
				else if (subsectorDto.getCode().equals("1300")) {
					CategoryDto subsectorDto1 = createCategoryDto("1310", "Agricultural machinery such as truck, tractors and other farm tools", "", 3L);
					CategoryDto subsectorDto2 = createCategoryDto("1320", "Water, Irrigation and supporting services", "", 3L);
					CategoryDto subsectorDto3 = createCategoryDto("1330", "Veterinary and related services", "", 3L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("1400")) {
					CategoryDto subsectorDto1 = createCategoryDto("1410", "Agricultural equipment and accessories", "", 4L);
					CategoryDto subsectorDto2 = createCategoryDto("1420", "Dealers in agro-chemicals, seeds and other farm inputs", "", 4L);
					CategoryDto subsectorDto3 = createCategoryDto("1430", "Distribution of farm produce", "", 4L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("1500")) {
					CategoryDto subsectorDto1 = createCategoryDto("1510", "Agro-forestry", "", 5L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("2100")) {
					CategoryDto subsectorDto1 = createCategoryDto("2110", "Wholesale", "", 6L);
					CategoryDto subsectorDto2 = createCategoryDto("2120", "Retail", "", 6L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
				}
				else if (subsectorDto.getCode().equals("2200")) {
					CategoryDto subsectorDto1 = createCategoryDto("2210", "Public service transport", "", 7L);
					CategoryDto subsectorDto2 = createCategoryDto("2220", "Purchase of motorvehicle accessories", "", 7L);
					CategoryDto subsectorDto3 = createCategoryDto("2230", "Transportation of goods", "", 7L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("2300")) {
					CategoryDto subsectorDto1 = createCategoryDto("2310", "Accomodation, restaurants, conference facilities, event planning & outside", "", 8L);
					CategoryDto subsectorDto2 = createCategoryDto("2320", "Schools and kindergartens", "", 8L);
					CategoryDto subsectorDto3 = createCategoryDto("2330", "Medical clinics and equipment", "", 8L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("2400")) {
					CategoryDto subsectorDto1 = createCategoryDto("2410", "Import", "", 9L);
					CategoryDto subsectorDto2 = createCategoryDto("2420", "Export", "", 9L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
				}
				else if (subsectorDto.getCode().equals("3100")) {
					CategoryDto subsectorDto1 = createCategoryDto("3110", "Jua kali Industry", "", 10L);
					CategoryDto subsectorDto2 = createCategoryDto("3120", "Small scale Agricultural Produce processing", "", 10L);
					CategoryDto subsectorDto3 = createCategoryDto("3130", "Dressmaking Industry", "", 10L);
					CategoryDto subsectorDto4 = createCategoryDto("3140", "Leather tanning", "", 10L);
					CategoryDto subsectorDto5 = createCategoryDto("3150", "Carving and handcrafts", "", 10L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
					createCategorySafely(subsectorDto4);
					createCategorySafely(subsectorDto5);
				}
				else if (subsectorDto.getCode().equals("3200")) {
					CategoryDto subsectorDto1 = createCategoryDto("3210", "Motor vehicle repairs", "", 11L);
					CategoryDto subsectorDto2 = createCategoryDto("3220", "Professional services such as Barber shops", "", 11L);
					CategoryDto subsectorDto3 = createCategoryDto("3230", "Working capital for learning institutions, churches & business enterprises", "", 11L);
					CategoryDto subsectorDto4 = createCategoryDto("3240", "Promotion of local tourism", "", 11L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
					createCategorySafely(subsectorDto4);
				}
				else if (subsectorDto.getCode().equals("3300")) {
					CategoryDto subsectorDto1 = createCategoryDto("3310", "Computer services and Internet", "", 12L);
					CategoryDto subsectorDto2 = createCategoryDto("3320", "Computer software and hardware", "", 12L);
					CategoryDto subsectorDto3 = createCategoryDto("3330", "Telecommunication Equipment", "", 12L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("4100")) {
					CategoryDto subsectorDto1 = createCategoryDto("4110", "School fees for primary and secondary schools including shopping and accommodation", "", 13L);
					CategoryDto subsectorDto2 = createCategoryDto("4120", "College fees, University fees, training fees, seminar fees", "", 13L);
					CategoryDto subsectorDto3 = createCategoryDto("4130", "Research and scientific activities etc", "", 13L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("5100")) {
					CategoryDto subsectorDto1 = createCategoryDto("5110", "Medical Bills, purchase of medicine", "", 14L);
					CategoryDto subsectorDto2 = createCategoryDto("5120", "Maternity Bills and expenses", "", 14L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
				}
				else if (subsectorDto.getCode().equals("6100")) {
					CategoryDto subsectorDto1 = createCategoryDto("6110", "Purchase of plots", "", 15L);
					CategoryDto subsectorDto2 = createCategoryDto("6120", "Land purchase services such as surveying and valuation", "", 15L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
				}
				else if (subsectorDto.getCode().equals("6200")) {
					CategoryDto subsectorDto1 = createCategoryDto("6210", "Construction of multiple residential buildings", "", 15L);
					CategoryDto subsectorDto2 = createCategoryDto("6220", "Construction of commercial buildings", "", 15L);
					CategoryDto subsectorDto3 = createCategoryDto("6230", "Construction of single residential dwelling units", "", 15L);
					CategoryDto subsectorDto4 = createCategoryDto("6240", "Renoivations of the buildings", "", 15L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
					createCategorySafely(subsectorDto4);
				}
				else if (subsectorDto.getCode().equals("7100")) {
					CategoryDto subsectorDto1 = createCategoryDto("7110", "Payment to microfinance loans", "", 16L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("7200")) {
					CategoryDto subsectorDto1 = createCategoryDto("7210", "Payment to Commercial bank loans", "", 17L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("7300")) {
					CategoryDto subsectorDto1 = createCategoryDto("7310", "Purchase of residential property/payments to mortgage loans in other financial institutions", "", 18L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("7400")) {
					CategoryDto subsectorDto1 = createCategoryDto("7410", "Payment to insurance policies", "", 19L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("7500")) {
					CategoryDto subsectorDto1 = createCategoryDto("7510", "Buying of Sacco shares", "", 20L);
					CategoryDto subsectorDto2 = createCategoryDto("7520", "purchase of quote shares, unquoted shares, treasury bills & bonds, commercial papers, unit trusts and other quoted public funds", "", 20L);
					CategoryDto subsectorDto3 = createCategoryDto("7530", "Paying personal debtsto non-registered institutions", "", 20L);

					createCategorySafely(subsectorDto1);
					createCategorySafely(subsectorDto2);
					createCategorySafely(subsectorDto3);
				}
				else if (subsectorDto.getCode().equals("8100")) {
					CategoryDto subsectorDto1 = createCategoryDto("8110", "Expenses incurred relating to car and electronic repairs, bills like electricity, sewer, water, telephone, decoder, personal debts to family members and friends etc.", "", 21L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("8200")) {
					CategoryDto subsectorDto1 = createCategoryDto("8210", "Household necessities like food, beverages and basic household products.", "", 21L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("8300")) {
					CategoryDto subsectorDto1 = createCategoryDto("8310", "Goods that do not wear out quickly like automobiles(cars), books, household(home appliances, consumer electronics, furniture, tools etc) sports equipment, jewellery, toys etc", "", 21L);

					createCategorySafely(subsectorDto1);
				}
				else if (subsectorDto.getCode().equals("8400")) {
					CategoryDto subsectorDto1 = createCategoryDto("8410", "Burial expenses, wedding expenses, rites of passage expenses.", "", 21L);

					createCategorySafely(subsectorDto1);
				}
			} catch (ValidationException validationException) {
				log.info("Error occurred while creating sub section");
			}
		}

	}



	private CategoryDto createCategoryDto(String categoryCode, String categoryName, String description, long subSectorId) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setCode(categoryCode);
		categoryDto.setName(categoryName);
		categoryDto.setDescription(description);
		categoryDto.setSubSectorId(subSectorId);

		return categoryDto;
	}

	private SectorDto createSectorDto(String code, String name, String description) {
		SectorDto sectorDto = new SectorDto();
		sectorDto.setCode(code);
		sectorDto.setName(name);
		sectorDto.setDescription(description);
		return sectorDto;
	}

	private SubsectorDto createSubSectorDto(String code, String name, String description, Long sectorId) {
		SubsectorDto subsectorDto = new SubsectorDto();
		subsectorDto.setCode(code);
		subsectorDto.setName(name);
		subsectorDto.setDescription(description);
		subsectorDto.setSectorId(sectorId);
		return subsectorDto;
	}

	private void createSubSectorSafely(SubsectorDto dto) {
		try {
			subsectorService.createSubsector(dto);
		} catch (ValidationException e) {
			log.error("Validation failed for code {} : {}",
					dto.getCode(),
					e.getMessage());

		} catch (Exception e) {
			log.error("Unexpected error for code {}",
					dto.getCode(),
					e);
		}
	}

	private void createCategorySafely(CategoryDto categoryDto) {
		try {
			categoryService.createCategory(categoryDto);
		} catch (ValidationException e) {
			log.error("Validation failed for creating category for code {} : {}",
					categoryDto.getCode(),
					e.getMessage());

		} catch (Exception e) {
			log.error("Unexpected error for code {}",
					categoryDto.getCode(),
					e);
		}
	}

}



