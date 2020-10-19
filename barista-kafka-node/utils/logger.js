const chalk = require('chalk');

const info = message => {
  console.log(`[${chalk.blue.bold('INFO')}] ${message}`);
};

const warning = message => {
  console.log(`[${chalk.yellow.bold('WARNING')}] ${message}`);
};

const error = message => {
  console.log(`[${chalk.red.bold('ERROR')}] ${message}`);
};

module.exports = {
  info,
  warning,
  error,
};
