# Sports Management Application

The Sports Management Application is a command-line tool for managing sports, events, markets, and selections.

## Getting Started

Follow these instructions to set up and run the Sports Management Application on your local machine.

### Prerequisites

- Java 8 or later
- Scala 2.12 or later
- sbt (Scala Build Tool) 1.4.0 or later

### Installation

1. Clone this repository to your local machine:

   ```bash
   git clone https://github.com/your-username/sports-management-app.git

### Navigate to the project directory:

cd sports-management-app

### Run the application

sbt run

Follow the on-screen prompts to interact with the application. You can add sports, events, markets, and selections, view
all sports, and fill with sample data.

### Using Docker

docker build -t sports-management-app
docker run -it sports-management-app

### File Persistence

The application uses CSV files for data persistence. Data is stored in the /data directory. The application reads and
writes data to the sports_data.json file.