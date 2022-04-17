package com.fundamentosplatzi.springboot.fundamentos;

import com.fundamentosplatzi.springboot.fundamentos.bean.MyBean;
import com.fundamentosplatzi.springboot.fundamentos.bean.MyBeanWithDependency;
import com.fundamentosplatzi.springboot.fundamentos.bean.MyBeanWithProperties;
import com.fundamentosplatzi.springboot.fundamentos.component.ComponentDependency;
import com.fundamentosplatzi.springboot.fundamentos.entity.User;
import com.fundamentosplatzi.springboot.fundamentos.pojo.UserPojo;
import com.fundamentosplatzi.springboot.fundamentos.repository.UserRepository;
import com.fundamentosplatzi.springboot.fundamentos.service.UserService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class FundamentosApplication implements CommandLineRunner {

	Log LOGGER = LogFactory.getLog(FundamentosApplication.class);

	private ComponentDependency componentDependency;
	private MyBean myBean;
	private MyBeanWithDependency myBeanWithDependency;
	private MyBeanWithProperties myBeanWithProperties;
	private UserPojo userPojo;
	private UserRepository userRepository;
	private UserService userService;

	public FundamentosApplication(
			@Qualifier("componentTwoImplement") ComponentDependency componentDependency,
			MyBean myBean,
			MyBeanWithDependency myBeanWithDependency,
			MyBeanWithProperties myBeanWithProperties,
			UserPojo userPojo,
			UserRepository userRepository,
			UserService userService
	) {
		this.componentDependency = componentDependency;
		this.myBean = myBean;
		this.myBeanWithDependency = myBeanWithDependency;
		this.myBeanWithProperties = myBeanWithProperties;
		this.userPojo = userPojo;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(FundamentosApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// ejemplosAnteriores();
		saveUsersInDataBase();
		// getInformationJpqlFromUser();
		saveWithErrorTransactional();
	}

	private void saveUsersInDataBase() {
		User user1 = new User("Boris", "boris@email.com", LocalDate.of(2021,03,20));
		User user2 = new User("Juan", "juan@email.com", LocalDate.of(2001,05,29));
		User user3 = new User("User3", "user3@email.com", LocalDate.of(2001,05,29));
		User user4 = new User("user4", "user4@email.com", LocalDate.of(2001,05,29));
		User user5 = new User("user5", "user5@email.com", LocalDate.of(2001,05,29));



		List<User> list = Arrays.asList(user1, user2, user3, user4, user5);
		list.stream().forEach(userRepository::save);
	}

	private void getInformationJpqlFromUser() {
		LOGGER.info("El usuario findByUserEmail: " + userRepository.findByUserEmail("boris@email.com")
				.orElseThrow(()-> new RuntimeException("No se encontro el usuario")));

		userRepository.findAndSort("user", Sort.by("id").descending())
				.forEach(user -> LOGGER.info("Usuario con metodo sort : " + user));

		userRepository.findByName("Boris")
				.forEach(user -> LOGGER.info("Usuario con queryMethod : " + user));

		LOGGER.info("Usuario con queryMethod : " + userRepository.findByEmailAndName("boris@email.com", "Boris")
				.orElseThrow(()-> new RuntimeException("Usuario no encontrado")));

		userRepository.findByNameLike("%u%")
				.forEach(user -> LOGGER.info("Usuario findByNameLike : " + user));

		userRepository.findByNameOrEmail("Boris", "lala@email.com")
				.forEach(user -> LOGGER.info("usuario findByNameOrEmail : " + user));

		LocalDate begin = LocalDate.of(2001,01,10 );
		LocalDate end = LocalDate.of(2022,12,10);
		userRepository.findByBirthDateBetween(begin, end)
				.forEach(user -> LOGGER.info("Usuaroo findByBirthDateBetween: " + user));

		userRepository.findByNameLikeOrderByIdDesc("%user%")
				.forEach(user -> LOGGER.info("Usuario findByNameLikeOrderByIdDesc : " + user));
		/*
		LOGGER.info("Usuario por named parameter : " + userRepository.getAllByBirthDateAndEmail(LocalDate.of(2021,01,10),"boris@email.com")
				.orElseThrow(()-> new RuntimeException("No se encontro el usuario apartir del named parameter")));
		*/
	}

	private void saveWithErrorTransactional() {
		User test1 = new User("testTransactional1", "test1@email.com", LocalDate.of(2020,01,10));
		User test2 = new User("testTransactional2", "test2@email.com", LocalDate.of(2021,01,10));
		User test3 = new User("testTransactional3", "test3@email.com", LocalDate.of(2022,01,10));
		User test4 = new User("testTransactional4", "test4@email.com", LocalDate.of(2023,01,10));

		List<User> users = Arrays.asList(test1,test2,test3,test4);

		try {
			userService.saveTransactional(users);
		} catch (Exception e){
			LOGGER.error("Esta es una exception dentro del metodo transaccional");
			// Realiza rollback cuando encuentra error
		}
		userService.getAllUsers()
				.forEach(user -> LOGGER.info("Este es el usuario dentro del metodo transactional : " + user));

	}

	private void ejemplosAnteriores() {
		componentDependency.saludar();
		myBean.print();
		myBeanWithDependency.printWithDependency();
		System.out.println(myBeanWithProperties.function());
		System.out.println(userPojo.getEmail() + " - " + userPojo.getPassword());
		LOGGER.error("Esto es un error");

		try {
			// error
			int value = 10/0;
			LOGGER.info("Mi valor: " + value);
		} catch (Exception e){
			LOGGER.error("Esto es un error usando try-catch : " + e.getMessage());
		}
	}
}
