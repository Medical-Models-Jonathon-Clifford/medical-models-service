// Required Modules
const fs = require('fs');
const path = require('path');

// Function to convert image to Base64
const convertImageToBase64 = (filePath) => {
    try {
        if (!fs.existsSync(filePath)) {
            console.error('Error: File does not exist.');
            return;
        }

        // Read the image file
        const fileContent = fs.readFileSync(filePath);

        // Convert to Base64
        const base64String = fileContent.toString('base64');

        return base64String;
    } catch (error) {
        console.error('Error converting file:', error.message);
        process.exit(1);
    }
};

// Main function
const main = () => {
    const filePath = process.argv[2]; // Get file path from command-line arguments

    if (!filePath) {
        console.error('Usage: node convert-to-base64.js <image-file-path>');
        process.exit(1);
    }

    const absolutePath = path.resolve(filePath);
    const base64String = convertImageToBase64(absolutePath);

    if (base64String) {
        console.log('Base64 Encoded String:\n');
        console.log(base64String);
    }
};

// Execute main function
main();