const pino = require('pino');
const Kafka = require('node-rdkafka');
const barista = require('./models/barista');

const logger = pino({
  prettyPrint: true
});

const consumer = new Kafka.KafkaConsumer(
  {
    'group.id': 'baristas',
    'metadata.broker.list': 'localhost:9092',
    'enable.auto.commit': true
  },
  {
    'auto.offset.reset': 'earliest'
  }
);

const producer = new Kafka.Producer({
  'client.id': 'barista-kafka-node',
  'metadata.broker.list': 'localhost:9092',
  dr_cb: true
});

consumer.on('ready', () => {
  logger.info('Consumer connected to kafka cluster');
  // subscribe consumer to a topic
  consumer.subscribe(['orders']);
  consumer.consume();
});

producer.on('ready', () => {
  logger.info('Producer connected to kafka cluster');

  consumer.on('data', async (message) => {
    const order = JSON.parse(message.value.toString());
    const beverage = await barista.prepare(order);

    logger.info(`Order ${order.orderId} for ${order.name} is ready`);

    try {
      producer.produce(
        'queue',
        null,
        Buffer.from(JSON.stringify({ ...beverage })),
        null,
        Date.now()
      );
    } catch (err) {
      logger.error('A problem occurred when sending our message');
      logger.error(err);
    }
  });
});

// without this, we do not get delivery events and the queue
producer.setPollInterval(100);

consumer.connect();
producer.connect();
