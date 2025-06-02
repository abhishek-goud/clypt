#!/usr/bin/env node
require('dotenv').config();
const { Command } = require('commander');
const FormData = require('form-data');
const axios = require('axios');
const fs = require('fs');
const path = require('path');
const chalk = require('chalk'); 

const program = new Command(); 
const ENDPOINT_URL = "http://localhost:8080/api/v1/clypt/anonymous";

// CLI Metadata
program
   .name('clypt')
   .description('CLI for Clypt application!')
   .version('1.1.0');

// Upload Command
program
   .command('upload <file>')
   .description('Upload a file using Clypt and retrieve unique code')
   .action(async (file) => {
      console.log(chalk.blue("🚀 CLYPT INITIATED"));
      console.log(chalk.yellow(`📦 Preparing file for secure transmission: ${file}`));

      const formData = new FormData();
      formData.append('files', fs.createReadStream(file));

      try {
         const response = await axios.post(ENDPOINT_URL, formData, {
            headers: {
               ...formData.getHeaders(),
            },
         });
         console.log({response})

         const { uniqueCode } = response.data;
         
         console.log(chalk.green('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
         console.log(chalk.green('✨ TRANSMISSION COMPLETE ✨'));
         console.log(chalk.green('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
         
         console.log(chalk.cyan(`🔐 SECRET ACCESS CODE: ${uniqueCode}`));
         console.log(chalk.gray(`Share this code with your intended recipient only`));
         console.log(chalk.gray(`Files are encrypted and will be available for 24 hours`));

      } catch (error) {
         console.log(chalk.red('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
         console.log(chalk.red('❌ TRANSMISSION FAILED ❌'));
         console.log(chalk.red('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
         
         console.error(chalk.red(`🔥 Error: ${error.message}`));
         console.error(chalk.red(`Check your network connection and try again`));
      }

      console.log("uploaded filessss",{formData})
   });

// Download Command
program
   .command('download <code>')
   .description('Download files associated with a code as a ZIP')
   .action(async (code) => {
      console.log(chalk.blue("🔍 CLYPT RECOVERY PROTOCOL"));
      console.log(chalk.yellow(`🔑 Validating access code: ${code}...`));

      try {
         const response = await axios({
            url: `${ENDPOINT_URL}?code=${code}`,
            method: 'GET',
            responseType: 'stream',
         });

         const outputPath = path.resolve(__dirname, `${code}.zip`);
         const writer = fs.createWriteStream(outputPath);

         console.log(chalk.yellow(`📥 Decrypting secure payload...`));
         
         response.data.pipe(writer);

         writer.on('finish', () => {
            console.log(chalk.green('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
            console.log(chalk.green('✅ RECOVERY SUCCESSFUL ✅'));
            console.log(chalk.green('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
            
            console.log(chalk.cyan(`📁 Secure package delivered: ${outputPath}`));
            console.log(chalk.gray(`Files have been safely retrieved and are ready for access`));
         });

         writer.on('error', (error) => {
            console.log(chalk.red('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
            console.log(chalk.red('❌ RECOVERY INTERRUPTED ❌'));
            console.log(chalk.red('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
            
            console.error(chalk.red(`💥 Download Error: ${error.message}`));
            console.error(chalk.red(`Check your disk space and write permissions`));
         });

      } catch (error) {
         console.log(chalk.red('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
         console.log(chalk.red('❌ RECOVERY FAILED ❌'));
         console.log(chalk.red('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'));
         
         console.error(chalk.red(`🔥 Error: ${error.message}`));
         console.error(chalk.red(`The code may be invalid or expired`));
         console.error(chalk.red(`Verify the code and try again`));
      }
   });

// Parse CLI arguments
program.parse(process.argv);