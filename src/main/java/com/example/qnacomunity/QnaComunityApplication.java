package com.example.qnacomunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QnaComunityApplication {

  public static void main(String[] args) {
    SpringApplication.run(QnaComunityApplication.class, args);
  }

}
