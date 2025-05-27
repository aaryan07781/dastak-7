# Dastak Mobile 7 - App Architecture

## Overview
Dastak Mobile 7 is an offline billing and sales analysis app for mobile shops with a 3D-style UI, dark/light theme support, and comprehensive product and sales management features.

## Architecture Pattern
The app will follow the MVVM (Model-View-ViewModel) architecture pattern for better separation of concerns and maintainability:

1. **Model**: Represents the data and business logic
   - Database entities for products, sales, bills
   - Repository classes for data operations
   - Business logic for calculations (profit, discounts)

2. **View**: UI components that display data
   - Activities and Fragments
   - XML layouts
   - Custom views for charts and 3D elements

3. **ViewModel**: Connects Model and View
   - Handles UI-related data logic
   - Manages UI state
   - Processes user actions

## Core Components

### 1. Database (Room)
- ProductEntity: Stores product information
- BillEntity: Stores bill information
- BillItemEntity: Stores items in a bill
- SalesEntity: Stores sales data for analysis

### 2. Repositories
- ProductRepository: Manages product data operations
- BillRepository: Manages bill creation and retrieval
- SalesRepository: Manages sales data for analysis

### 3. ViewModels
- ProductViewModel: Manages product listing and operations
- BillViewModel: Manages bill creation and calculations
- SalesViewModel: Manages sales data and analytics
- SettingsViewModel: Manages app settings and theme

### 4. UI Components
- MainActivity: Main container with navigation
- SplashActivity: Initial splash screen with animations
- ProductFragment: Product management
- BillFragment: Bill creation and management
- SalesFragment: Sales and profit analysis
- SettingsFragment: App settings and theme toggle
- ContactFragment: Contact support information

### 5. Utilities
- PDFGenerator: Generates PDF bills
- DriveBackupManager: Handles Google Drive backup
- ThemeManager: Manages theme switching
- TrialManager: Handles trial period and activation
- CalculationUtils: Handles price and profit calculations

## Data Flow
1. User adds products through ProductFragment
2. Products are stored in the database via ProductRepository
3. User creates bills by selecting products from BillFragment
4. Bill data is processed by BillViewModel and stored via BillRepository
5. Sales data is extracted from bills and stored for analysis
6. SalesViewModel retrieves and processes data for charts and analytics

## External Integrations
- Google Drive API for backup and restore
- PDF generation library for bill creation
- Chart library for sales visualization

## Security Features
- Developer mode activation with credential verification
- Trial period management
- Data encryption for sensitive information

## Theme Management
- Dark and light theme support
- Theme toggle in settings
- 3D-style UI elements across all screens
