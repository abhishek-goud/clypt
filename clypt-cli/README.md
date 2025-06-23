# Clypt CLI

A secure command-line interface for uploading and downloading files with encryption. Clypt provides temporary file storage with unique access codes.

## Features

- **Secure File Upload**: Files are encrypted and stored securely on remote servers
- **Unique Access Codes**: Each upload generates a unique code for file retrieval
- **Temporary Storage**: Files expire after 18 hours
- **ZIP Archive Downloads**: Multiple files are packaged into convenient ZIP archives
- **Multiple File Support**: Upload multiple files in a single command

## Installation

Install Clypt globally using npm:

```bash
npm install -g clypt
```

## Usage

### Upload Files

Upload one or more files and receive a unique access code:

```bash
clypt upload <file1> <file2> <file3> ...
```

**Examples:**

```bash
# Upload a single file
clypt upload "/path/to/document.pdf"

# Upload multiple files
clypt upload "/path/to/file1.txt" "/path/to/file2.jpg" "/path/to/folder/file3.pdf"

```

### Download Files

Download files using the unique access code:

```bash
clypt download <code>
```

**Example:**

```bash
clypt download AB1234XY
```

## File Handling

- **Upload**: Files are processed individually and stored with encryption
- **Download**: All files associated with a code are packaged into a single ZIP archive
- **Expiration**: Files automatically expire after 18 hours
- **File Types**: Supports all file types and formats
- **Size Limits**: Depends on server configuration

## Security Features

- **Encryption**: Files are encrypted using secure algorithms before storage
- **Temporary Storage**: Automatic file deletion after 18 hours
- **Unique Codes**: Each upload generates a access code


## Requirements

- Node.js

## Dependencies

- `commander`: Command-line interface framework
- `axios`: HTTP client for API requests
- `form-data`: Multipart form data handling
- `chalk`: Terminal styling and colors
- `dotenv`: Environment variable management
