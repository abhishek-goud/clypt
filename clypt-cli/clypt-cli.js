#!/usr/bin/env node
require("dotenv").config();
const { Command } = require("commander");
const FormData = require("form-data");
const axios = require("axios");
const fs = require("fs");
const path = require("path");
const chalk = require("chalk");

const program = new Command();
const ENDPOINT_URL = "http://localhost:8080/api/clypt/anonymous";
const spinner = ["⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"]; // Loading animation frames
const clearSpinner = () => process.stdout.write("\r\x1b[K"); // Clear current line
const logHeader = (action, target) => {
  console.log(
    chalk.magenta(
      `[CLYPT] ${new Date().toLocaleString("en-GB", {
        dateStyle: "short",
        timeStyle: "medium",
      })}`
    )
  );
  console.log(chalk.white(`→ ${action}: ${target}`));
};

program
  .command("upload <files...>")
  .description(
    "Upload multiple files by appending locations spearated by space and retrieve unique code"
  )
  .action(async (files) => {
    logHeader("Processing files", files.join(", "));

    // Prepare files for upload
    const formData = new FormData();
    files.forEach((file) =>
      formData.append("files", fs.createReadStream(file))
    );

    let uploadInterval,
      i = 0;

    try {
      // Show upload spinner
      uploadInterval = setInterval(() => {
        process.stdout.write(
          `\r${chalk.hex("#3b82f6")(spinner[i++ % spinner.length])} Uploading`
        );
      }, 80);

      const response = await axios.post(ENDPOINT_URL, formData, {
        headers: formData.getHeaders(),
      });

      clearInterval(uploadInterval);
      clearSpinner();

      console.log(chalk.green(`Upload completed successfully`));
      console.log(
        chalk.white(`Code: `) + chalk.bold.cyan(response.data.uniqueCode)
      );
      console.log(chalk.dim(`Expires: 24 hours`));
    } catch (error) {
      clearInterval(uploadInterval);
      clearSpinner();
      console.log(
        chalk.red(
          `Upload failed: ${error?.response?.data?.message || error.message}`
        )
      );
    }
  });

program
  .command("download <code>")
  .description("Download files associated with a code as a ZIP")
  .action(async (code) => {
    logHeader("Retrieving", code);

    let validationInterval,
      downloadInterval,
      i = 0,
      j = 0;

    try {
      // Show validation spinner
      validationInterval = setInterval(() => {
        process.stdout.write(
          `\r${chalk.hex("#3b82f6")(
            spinner[i++ % spinner.length]
          )} Validating access code`
        );
      }, 80);

      const response = await axios({
        url: `${ENDPOINT_URL}?code=${code}`,
        method: "GET",
        responseType: "stream",
      });

      clearInterval(validationInterval);
      clearSpinner();

      const outputPath = path.resolve(__dirname, `${code}.zip`);
      const writer = fs.createWriteStream(outputPath);

      // Show download spinner
      downloadInterval = setInterval(() => {
        process.stdout.write(
          `\r${chalk.hex("#3b82f6")(
            spinner[j++ % spinner.length]
          )} Downloading zip file...`
        );
      }, 80);

      response.data.pipe(writer);

      writer.on("finish", async () => {
        clearInterval(downloadInterval);
        clearSpinner();

        // Get file types (optional)
        try {
          const fileType = await axios.get(
            `${ENDPOINT_URL}/filetype?code=${code}`
          );
          if (fileType.data?.length > 0)
            console.log(chalk.bold.cyan(`Files Type: ${fileType.data}`));
        } catch {}

        console.log(chalk.green(`Download completed successfully`));
        console.log(chalk.white(`Location: `) + chalk.bold.cyan(outputPath));
      });

      writer.on("error", (error) => {
        clearInterval(downloadInterval);
        clearSpinner();
        console.log(chalk.red(`Download interrupted: ${error.message}`));
      });
    } catch (error) {
      // Cleanup on error
      if (validationInterval) clearInterval(validationInterval);
      if (downloadInterval) clearInterval(downloadInterval);
      clearSpinner();
      console.log(chalk.red(`Download failed: ${error.message}`));
      console.log(
        chalk.dim(`Troubleshoot: Verify code validity and network connection`)
      );
    }
  });

program.parse(process.argv);
