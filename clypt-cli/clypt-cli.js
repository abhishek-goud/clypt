#!/usr/bin/env node
require('dotenv').config();
const { Command } = require('commander');
const FormData = require('form-data');
const axios = require('axios');
const fs = require('fs');
const path = require('path');
const chalk = require('chalk'); 

const program = new Command(); 
const ENDPOINT_URL = "http://localhost:8080/api/clypt/anonymous";




// CLI Metadata
program
   .command('upload <file>')
   .description('Upload a file using Clypt and retrieve unique code')
   .action(async (file) => {
      console.log(chalk.magenta(`[CLYPT] ${new Date().toISOString()}`));
      console.log(chalk.white(`→ Processing: ${file}`));

      const formData = new FormData();
      formData.append('files', fs.createReadStream(file));

      try {
         const spinner = ['⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'];
         let i = 0;
         const uploadInterval = setInterval(() => {
            process.stdout.write(`\r${chalk.hex('#3b82f6')(spinner[i++ % spinner.length])} Uploading`);
         }, 80);
         
         const response = await axios.post(ENDPOINT_URL, formData, {
            headers: {
               ...formData.getHeaders(),
            },
         });

         clearInterval(uploadInterval);
         process.stdout.write('\r \r');

         const { uniqueCode } = response.data;
         
         console.log(chalk.green(`✓ Upload completed successfully`));
         console.log(chalk.white(`Code: `) + chalk.bold.cyan(uniqueCode));
         console.log(chalk.dim(`Expires: 24 hours`));

      } catch (error) {
         console.log(chalk.red(`✗ Upload failed: ${error.message}`));
         console.log(chalk.dim(`Check network connection`));
      }
   });



// Download Command
program
   .command('download <code>')
   .description('Download files associated with a code as a ZIP')
   .action(async (code) => {
      console.log(chalk.magenta(`[CLYPT] ${new Date().toISOString()}`));
      console.log(chalk.white(`→ Retrieving: ${code}`));

      try {
         const spinner = ['⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'];
         let i = 0;
         const validationInterval = setInterval(() => {
            process.stdout.write(`\r${chalk.hex('#3b82f6')(spinner[i++ % spinner.length])} Validating access code`);
         }, 80);

         const response = await axios({
            url: `${ENDPOINT_URL}?code=${code}`,
            method: 'GET',
            responseType: 'stream',
         });

         clearInterval(validationInterval);
         process.stdout.write('\r \r');

         const outputPath = path.resolve(__dirname, `${code}.zip`);
         const writer = fs.createWriteStream(outputPath);

         let j = 0;
         const downloadInterval = setInterval(() => {
            process.stdout.write(`\r${chalk.hex('#3b82f6')(spinner[j++ % spinner.length])} Downloading package...`);
         }, 80);
         
         response.data.pipe(writer);
         
         writer.on('finish', async () => {
            clearInterval(downloadInterval);
            process.stdout.write('\r \r');
            
            console.log(chalk.green(`✓ Download completed successfully`));
            console.log(chalk.white(`Location: `) + chalk.bold.cyan(outputPath));
            
            try {
               const res = await axios.get(`${ENDPOINT_URL}/filetype?code=${code}`);
               const fileType = res.data || 'unknown';
               console.log(chalk.white(`Type: `) + chalk.cyan(fileType));
            } catch (e) {
               console.log(chalk.dim('Type: unable to determine'));
            }
            
            console.log(chalk.dim(`Status: Ready for extraction`));
         });

         writer.on('error', (error) => {
            clearInterval(downloadInterval);
            process.stdout.write('\r \r');
            console.log(chalk.red(`✗ Download interrupted: ${error.message}`));
         });

      } catch (error) {
         console.log(chalk.red(`✗ Download failed: ${error.message}`));
         console.log(chalk.dim(`Troubleshoot: Verify code validity and network connection`));
      }
   });

// Parse CLI arguments
program.parse(process.argv);