# Aspectow Demo Site: A Comprehensive Reference Application

This repository contains the source code for the Aspectow Demo Site, a collection of reference applications designed to illustrate best practices and common use cases for developing applications with the Aspectran framework. It showcases various features of Aspectran, including web application development, RESTful API creation, and integration with other technologies.

## Web Applications Demonstrated

This demo site showcases 4 distinct web applications:

*   JPetStore Demo
*   PetClinic Demo
*   Aspectran Examples
*   Aspectow AppMon

## Features

*   **Web Application Development:** Demonstrates how to build dynamic web applications using Aspectran's Translet and View technologies.
*   **RESTful APIs:** Examples of creating robust and scalable RESTful services.
*   **Modular Design:** Illustrates how to structure Aspectran applications for maintainability and scalability.

## Getting Started

To run the Aspectow Demo Site, follow these steps:

### Prerequisites

*   Java Development Kit (JDK) 21 or higher
*   Apache Maven 3.8.x or higher
*   Git

### Running with Aspectran Shell (Recommended)

This method uses a pre-configured Aspectran Shell distribution, providing a quick way to get the demo up and running.

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/aspectran/aspectow-demo.git
    ```

2.  **Navigate to the project directory:**
    ```sh
    cd aspectow-demo
    ```

3.  **Build the project with Maven:**
    ```sh
    mvn clean package
    ```
    This command will compile the project and package the application.

4.  **Start the Aspectran Shell:**
    ```sh
    cd app/bin
    ./shell.sh  # For Linux/macOS
    shell.cmd   # For Windows
    ```
    The application will start, and you should see log messages indicating that the web server is running.

5.  **Access the demo site:**
    Open your web browser and navigate to:
    [http://localhost:8080](http://localhost:8080)

### Stopping the Application

To stop the running application, type `quit` in the terminal where the Aspectran Shell is running and press Enter.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE.txt](LICENSE.txt) file for details.
