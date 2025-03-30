# Video Assembly Monitor （Spring boot Backend）

Welcome to the **Video Assembly Monitor** project! This tool is designed to help with the monitoring and tracking of assembly processes using video feeds, object detection, and gesture recognition.

## Javadoc Documentation

The Javadoc for this project is available online. You can explore the generated API documentation for detailed class and method information.

- [Javadoc Documentation](https://ltseed.github.io/VideoAssemblyMonitor/)

## REST API Documentation

For the backend API, a detailed REST API documentation is available. This includes all available endpoints, their parameters, and responses.

- [REST API Documentation on Postman](https://www.postman.com/satellite-astronaut-90149468/my-workspace/collection/l4oh81m/api-documentation?action=share&creator=42644368)

## A Simple Roboflow SDK 

A Simple Roboflow SDK for Java is included in the `src/main/java/nusri/fyp/demo/roboflow` path as a component of Spring Boot App.

If you are finding a Roboflow SDK for Java, you can just copy the code, add the OKHttp dependence and use.

If you don't want to use it in Spring Boot environment, you can easily config `RequestSenderOfOKHttp` and `RoboflowConfig` by giving them a new ObjectMapper, and Roboflow api host & port & apikey.

## Project Overview

The **Video Assembly Monitor** project is designed to help monitor and track assembly processes using advanced computer vision techniques like **hand tracking** and **object detection**. This system provides real-time feedback for assembly verification and improvement by analyzing video feeds from assembly lines or workshops. The system consists of a **backend** built with **Spring Boot** and a **frontend** built with **Vite**, **React**, **TypeScript**, and **Ant Design**.

### Core Features
- **Hand Tracking**: The project utilizes **MediaPipe** for detecting and tracking the user's hand gestures during the assembly process. This allows the system to recognize gestures such as grabbing and placing parts.
- **Object Detection**: **YOLO (You Only Look Once)** is employed to detect and identify objects (such as parts and tools) involved in the assembly process.
- **Real-time Monitoring**: The system provides real-time updates and feedback based on the visual analysis of assembly steps.
- **Progress Tracking**: Visual progress tracking for assembly tasks, showing a step-by-step guide and the current state (e.g., in progress, completed).
- **Interactive Interface**: The frontend offers a user-friendly interface to visualize assembly progress, monitor the status of various parts, and interact with the system for efficient management.

### Frontend

The frontend is available in a separate repository, which can be found [here](https://github.com/LtSeed/VideoAssemblyMonitorWeb). This repository contains the entire codebase for the web frontend, including pages for monitoring assembly progress, viewing real-time video feeds, and interacting with the backend.

## Backend Technology Stack

The backend of the **Video Assembly Monitor** project is built using **Spring Boot** with various dependencies to support data management, web services, real-time communication, and more. Below are the key components used in the backend:

### Core Dependencies:
- **Spring Boot**:
  - `spring-boot-starter-data-rest`: Provides easy integration of Spring Data JPA with RESTful APIs.
  - `spring-boot-starter-web`: A core dependency to build web applications with REST APIs using Spring MVC.
  - `spring-boot-starter-websocket`: Supports WebSocket communication for real-time bidirectional communication.
  - `spring-boot-starter-actuator`: Provides production-ready features like health checks and metrics.
  - `spring-boot-starter-data-jpa`: Integration of Spring Data JPA for accessing databases using Java Persistence API.
  - `spring-boot-starter-jdbc`: Provides JDBC support for database interaction.
  - `spring-cloud-starter`: Integrates Spring Cloud for microservices architecture and cloud-based functionality.
  - `spring-cloud-starter-config`: Used for externalized configuration management in a cloud environment.

### Computer Vision & External Libraries:
- **OpenCV**:
  - `org.openpnp:opencv:4.9.0-0`: The OpenCV library is used for image processing and computer vision tasks, including object detection and hand tracking.

### HTTP Communication:
- **OkHttp**:
  - `com.squareup.okhttp3:okhttp:3.14.+`: Used for making HTTP requests to external services, such as fetching data from APIs or communicating with other systems.

### Database & Persistence:
- **MySQL Connector**:
  - `runtimeOnly 'com.mysql:mysql-connector-j'`: Provides MySQL database connectivity for the application.
  
- **Lombok**:
  - `compileOnly 'org.projectlombok:lombok'`: Lombok reduces boilerplate code (e.g., getters, setters, constructors).
  - `annotationProcessor 'org.projectlombok:lombok'`: Required for annotation processing during compile-time.

### Development Tools:
- **Spring Boot DevTools**:
  - `developmentOnly 'org.springframework.boot:spring-boot-devtools'`: Provides additional development features, like hot swapping and automatic restarts.

### Testing:
- **JUnit & Spring Test**:
  - `testImplementation 'org.springframework.boot:spring-boot-starter-test'`: Essential for writing unit tests and integration tests with Spring Boot.
  - `testRuntimeOnly 'org.junit.platform:junit-platform-launcher'`: Provides runtime support for JUnit tests.

## Build From Source

To set up and run the **Video Assembly Monitor** project, we recommend using IntelliJ IDEA for a smooth and easy experience. Below are the detailed steps to get the project up and running using Gradle and Spring Boot with Java 17.

### Prerequisites

Before setting up the project, ensure you have the following installed on your system:

1. **IntelliJ IDEA**: You can download and install IDEA from [here](https://www.jetbrains.com/idea/download/).
2. **JDK 17**: This project requires JDK 17. You can download it from [AdoptOpenJDK](https://adoptopenjdk.net/) or [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).
3. **MySQL**: Project need MySQL installed. You can download it from [here](https://dev.mysql.com/downloads/workbench/) or [here](https://dev.mysql.com/downloads/windows/installer/8.0.html).
4. **OpenCV**: OpenCV460 is needed. The config guide will be in the 7th part of this section.
5. **Gradle**: While IntelliJ IDEA can automatically manage Gradle, if you prefer to install Gradle separately, download it from [here](https://gradle.org/install/). (I prefer to let IntelliJ IDEA automatically manage Gradle.)

### 1. Clone the Repository

Start by cloning the project repository from GitHub. Download the whole project, and unzip it to a place you like.

### 2. Open the Project in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. On the welcome screen, select **Open**.
3. Navigate to the folder where you unzip the project.
4. IDEA will automatically detect that this is a Gradle project and will prompt you to import Gradle. Click **Import Gradle Project** or **Enable Auto-Import** to proceed.
5. If you miss the hint of import Gradle project, you can just close the IDE and open the project again. Then import hint will show again.
6. Be patient when importing. When import done, the icons on the IDE of most of java files will not be an orange tea cup (while they are all orange tea cups when importing). 

### 3. Sync Gradle

After opening the project, IntelliJ IDEA will attempt to automatically sync Gradle. If it doesn’t, manually sync the project by following these steps:

1. In the **Gradle** tool window (usually on the right side of IDEA, looks like an elephant), click the **Refresh** button (the circular arrow icon) to sync the project with Gradle.
2. Patiently wait for the sync process to complete.

### 4. Configure Java 17

Make sure the project is using Java 17 as the runtime (Because there may be many versions of java on your PC, while you don't know). In IntelliJ IDEA, follow these steps:

1. Go to **File > Project Structure > Project**.
2. In the **Project SDK** dropdown, select **JDK 17**.
3. Click **Apply** and then **OK**.

### 5. Build the Project with Gradle

To build the project and generate Javadoc, open the **Gradle** tool window, expand **Tasks > build**, and run the `build` task. 

This will compile the project and ensure all dependencies are correctly set up.

### 6. Config the database

Connect your database with your IDE. And create a new dataset.

``You can find a guide everywhere but not here.``

Run the script at `src/main/java/nusri/fyp/demo/entity/update-schema.sql` in your database shell, it will auto create tables in the form the app needed.

Open `src/main/resources/application.properties`

Find the lines below:

```
spring.datasource.url=jdbc:mysql://localhost:3306/{datasetName}
spring.datasource.username=root
spring.datasource.password=root
```

Replace them with your dataset url, username and password.

### 7. Config OpenCV DLL

Download and put the path below in `PATH` system var.
```
1.{your extracted path of openCV}\opencv\build\x64\vc14\bin
2.{your extracted path of openCV}\opencv\build\x64\vc15\bin
3.{your extracted path of openCV}\opencv\build\java\x64  
4.{your extracted path of openCV}\opencv\build\java\x64  
```

Please select the third and fourth items according to your system.

The link below is for Windows. 

Download openCV 4.6.0 [here](https://sourceforge.net/projects/opencvlibrary/files/4.6.0/opencv-4.6.0-vc14_vc15.exe/download).


### 8. Run the Spring Boot Application

Once the project is built, you can run the Spring Boot application. The main entry point for the application is `nusri.fyp.demo.DemoApplication`. To run the application with Java 17, follow these steps:

#### Using IntelliJ IDEA:

1. In IntelliJ IDEA, open the **Run/Debug Configurations** dialog by clicking on the **Run** dropdown in the top-right corner and selecting **Edit Configurations**.
2. Click on the **+** icon and select **Spring Boot**.
3. In the **Main class** field, enter `nusri.fyp.demo.DemoApplication`.
4. In the **-cp** field, select demo.main.
5. Ensure the **JRE** is set to **JDK 17**.
6. Click **OK** and then click the **Run** button (green triangle) in the top-right corner of the IDE.


### Troubleshooting

If you encounter any issues during setup, consider the following:

- Ensure that JDK 17 or higher is properly installed and selected as the project SDK in IntelliJ IDEA. You can check this in **File > Project Structure > Project > Project SDK**.
- If Gradle fails to sync, check that your internet connection is stable and Gradle is properly installed or configured in IDEA.
- If the project fails to build, try running `./gradlew clean build` from the terminal in IDEA to clean up any previous build artifacts.
- Ensure that the correct `-cp` classpath is set when running the application from the terminal (especially if you're using the JAR file).


## Contributing

If you'd like to contribute to this project, feel free to fork the repository and create pull requests. Any improvements or bug fixes are always appreciated.

### Steps to Contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature-name`)
3. Make your changes
4. Commit your changes (`git commit -am 'Add new feature'`)
5. Push to the branch (`git push origin feature-name`)
6. Create a new pull request

### For those who continues to do this project in next year in NUSRI:

You can contact me to add you as `Collaborators` of this repo.


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This project would not have been possible without the support and contributions from the following:

- **Supervisor**: Special thanks to my supervisor, Ong SohKhim, for the invaluable guidance and feedback throughout the development of this project.
- **Teammate**: Thanks to my teammate, Cai Yunchen, for building the model and other help.
- **Previous Researchers**: For providing a general methodology, dataset, model and optimization ideas for this project.
- **Libraries & Frameworks**:
  - **YOLO**: For providing an efficient object detection algorithm that powers the real-time object recognition in the assembly process.
  - **MediaPipe**: For enabling precise hand gesture recognition that enhances the user interaction with the system.
  - **OpenCV**: For serving as the core computer vision library that enables video processing and object detection tasks.
  - **Spring Boot**: For offering a powerful framework for building the backend services of the project with minimal configuration.
  - **Vite**: For providing a fast and optimized build tool that supports the frontend of the project.
  - **React**: For offering a component-based architecture that makes building the frontend intuitive and maintainable.
  - **Ant Design**: For supplying high-quality UI components that contributed to the smooth user interface design.
  - **MySQL**: For providing the database support to manage and store assembly process data.
  - **Gradle**: For automating the build and dependency management of the project.
  - **JUnit**: For helping ensure the backend code is thoroughly tested and reliable.
- **Everyone who provided feedback**: Your insights helped improve the quality and direction of this project.

This project is part of my **FYP (Final Year Project)** at NUSRI@Suzhou and Chongqing University, and other acknowledgments will be listed in my **CA2 Report**.

---

For further information, please refer to the provided links for Javadoc and REST API documentation.

Feel free to reach out for any questions or suggestions. Enjoy using **Video Assembly Monitor**!


