# SHA Mobiles Service Management App

A mobile application for managing mobile phone service records and accessory sales at SHA Mobiles. The app provides different interfaces for owners, managers, and mechanics to handle their respective responsibilities.

## Features

### Owner Dashboard
- View all service records
- Track individual mechanic services (Elumalai and Nizar)
- Monitor accessory sales history
- View overall revenue statistics

### Manager Dashboard
- Monitor services by both mechanics (Elumalai and Nizar)
- Add new accessory sales
- View sales history
- Track revenue from services and sales

### Mechanic Interface
- Add new service records
- View service history
- Track individual service records

## Technical Details

### Built With
- Java
- Android SDK
- Firebase Firestore (Database)
- Firebase Authentication

### Key Components

#### Authentication
- Role-based access control (Owner, Manager, Mechanic)
- Secure login system

#### Service Management
- Create and track mobile phone service records
- Associate services with specific mechanics
- Track service status and pricing

#### Sales Management
- Record accessory sales
- Track sales by manager
- Monitor sales history and revenue

#### Revenue Tracking
- Real-time revenue calculations
- Separate tracking for services and accessory sales
- Individual mechanic revenue tracking

### Database Structure

#### Collections
- `users`: Store user information and roles
- `services`: Mobile phone service records
- `accessory_sales`: Accessory sales records

## Installation

1. Clone the repository
2. Open the project in Android Studio
3. Configure Firebase:
   - Add your `google-services.json`
   - Enable Authentication and Firestore
4. Build and run the application

## Usage

### Owner Access
- Login with owner credentials
- Navigate through tabs to view different sections
- Monitor overall business performance

### Manager Access
- Login with manager credentials
- Add new accessory sales
- View sales history and mechanic services

### Mechanic Access
- Login with mechanic credentials
- Add new service records
- View personal service history

## Security

- Role-based access control
- Secure data management
- Firebase Authentication integration

## Contributing

This is a private project for SHA Mobiles. Please contact the owner for any modifications or improvements.

## License

This project is proprietary software. All rights reserved.
