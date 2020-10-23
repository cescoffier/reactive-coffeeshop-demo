const { pickName, getPreparationTime } = require('../utils/random');

const barista = pickName();

const state = {
  IN_QUEUE: 'IN_QUEUE',
  BEING_PREPARED: 'BEING_PREPARED',
  READY: 'READY'
};

const prepare = order => new Promise((resolve) => {
  const delay = getPreparationTime();
  setTimeout(
    () => resolve({
      orderId: order.orderId,
      beverage: order.product,
      customer: order.name,
      preparedBy: barista,
      preparationState: state.READY
    }),
    delay
  );
});

module.exports = {
  name: barista,
  prepare
};
