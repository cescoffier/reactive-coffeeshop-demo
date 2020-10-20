const { Kafka } = require('kafkajs');
const logger = require('./utils/logger');
const barista = require('./models/barista');

const kafka = new Kafka({
  clientId: 'barista-kafka-node',
  brokers: ['localhost:9092'],
});

const consumer = kafka.consumer({ groupId: 'baristas' });
const producer = kafka.producer();

const run = async () => {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({ topic: 'orders', fromBeginning: true });

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const order = JSON.parse(message.value.toString());

      const beverage = await barista.prepare(order);
      logger.info(`Order ${order.orderId} for ${order.name} is ready`);

      await producer.connect();
      await producer.send({
        topic: 'queue',
        messages: [{ value: JSON.stringify({ ...beverage }) }],
      });
    },
  });
};

run().catch(e => logger.error(e.message));
