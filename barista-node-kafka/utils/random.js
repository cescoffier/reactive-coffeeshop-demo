const names = [
  'Olivia',
  'Oliver',
  'Amelia',
  'George',
  'Isla',
  'Harry',
  'Ava',
  'Noah',
  'Emily',
  'Jack',
  'Sophia',
  'Charlie',
  'Grace',
  'Leo',
  'Mia',
  'Jacob',
  'Poppy',
  'Freddie',
  'Ella',
  'Alfie',
  'Tom',
  'Julie',
  'Matt',
  'Joe',
  'Zoe'
];

function getRandomInt (max) {
  return Math.floor(Math.random() * Math.floor(max));
}

function pickName () {
  const index = getRandomInt(names.length);
  return names[index];
}

function getPreparationTime () {
  return getRandomInt(5) * 1000;
}

module.exports = {
  pickName,
  getPreparationTime
};
