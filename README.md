Sports Management Application
The Sports Management Application is a command-line tool for managing sports, events, markets, and selections.

Getting Started
Follow these instructions to set up and run the Sports Management Application on your local machine.

Prerequisites
Java 8 or later
Scala 2.12 or later
sbt (Scala Build Tool) 1.4.0 or later
Installation
Clone this repository to your local machine:

bash
Copy code
git clone https://github.com/your-username/sports-management-app.git
Navigate to the project directory:

bash
Copy code
cd sports-management-app
Usage
Build the project using sbt:

bash
Copy code
sbt compile
Run the application:

bash
Copy code
sbt run
Follow the on-screen prompts to interact with the application. You can add sports, events, markets, and selections, view all sports, and fill with sample data.

To exit the application, select the "Exit" option from the menu.

Features
Add sports, events, markets, and selections to the application.
View all existing sports and their associated events, markets, and selections.
Fill the application with sample data for testing and demonstration purposes.
File Persistence
The application uses CSV files for data persistence. Data is stored in the src/main/resources directory. The application reads and writes data to the sports_data.csv file.

Contributing
Contributions are welcome! If you have suggestions, bug reports, or feature requests, please open an issue on this repository.

License
This project is licensed under the MIT License. See the LICENSE file for details.

Acknowledgments
This application was created as part of a programming exercise. It showcases the use of Scala, sbt, and basic command-line interaction for managing sports data.

Feel free to customize the README further to include specific details about your project, such as installation steps, usage examples, additional features, and more.