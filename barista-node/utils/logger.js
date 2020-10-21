const chalk = require('chalk');

function info(message) {
  console.log(`[${chalk.blue.bold('INFO')}] ${message}`);
}

function warning(message) {
  console.log(`[${chalk.yellow.bold('WARNING')}] ${message}`);
}

function error(message) {
  console.log(`[${chalk.red.bold('ERROR')}] ${message}`);
}

module.exports = {
  info,
  warning,
  error,
};
