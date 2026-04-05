package com.naukri.clone.config;

import com.naukri.clone.model.Job;
import com.naukri.clone.model.User;
import com.naukri.clone.repository.JobRepository;
import com.naukri.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Create demo employer
            User employer = User.builder()
                    .fullName("TechCorp India")
                    .email("employer@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .role(User.UserRole.EMPLOYER)
                    .location("Bangalore")
                    .phone("9876543210")
                    .active(true)
                    .build();
            userRepository.save(employer);

            // Create demo job seeker
            User seeker = User.builder()
                    .fullName("Rahul Sharma")
                    .email("seeker@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .role(User.UserRole.JOB_SEEKER)
                    .location("Delhi")
                    .phone("9876543211")
                    .skills("Java, Spring Boot, MySQL, REST API")
                    .experience("2 years")
                    .active(true)
                    .build();
            userRepository.save(seeker);

            // Create demo jobs
            String[] titles = {"Senior Java Developer", "React Frontend Engineer", "Data Analyst", "Product Manager", "UI/UX Designer", "DevOps Engineer"};
            String[] companies = {"TechCorp India", "Infosys", "TCS", "Wipro", "Flipkart", "Zomato"};
            String[] locations = {"Bangalore", "Mumbai", "Delhi", "Hyderabad", "Pune", "Chennai"};
            String[] salaries = {"₹12-18 LPA", "₹8-14 LPA", "₹6-10 LPA", "₹15-25 LPA", "₹7-12 LPA", "₹10-16 LPA"};
            String[] skills = {"Java, Spring Boot, MySQL, Microservices", "React, JavaScript, CSS, Node.js", "Python, SQL, Excel, Tableau", "Agile, Roadmap, Analytics, Jira", "Figma, Adobe XD, CSS, Sketch", "AWS, Docker, Kubernetes, CI/CD"};
            String[] categories = {"IT", "IT", "IT", "Marketing", "Design", "IT"};

            for (int i = 0; i < titles.length; i++) {
                Job job = Job.builder()
                        .title(titles[i])
                        .company(companies[i])
                        .location(locations[i])
                        .salary(salaries[i])
                        .experience("2-5 years")
                        .jobType("Full-time")
                        .category(categories[i])
                        .description("We are looking for a talented " + titles[i] + " to join our growing team at " + companies[i] + ".\n\nYou will work on exciting projects, collaborate with cross-functional teams, and help drive our technology vision forward.\n\nThis is an excellent opportunity to grow your career with one of India's top companies.")
                        .requirements("• Bachelor's degree in Computer Science or related field\n• Strong problem-solving skills\n• Good communication skills\n• Experience with agile methodologies\n• Passion for technology and innovation")
                        .skills(skills[i])
                        .employer(employer)
                        .active(true)
                        .applicationCount(i * 3 + 2)
                        .build();
                jobRepository.save(job);
            }
            System.out.println("✅ Demo data initialized successfully!");
            System.out.println("📧 Employer login: employer@demo.com / demo123");
            System.out.println("📧 Job Seeker login: seeker@demo.com / demo123");
        }
    }
}
