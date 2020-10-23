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
  // log successful connection
  logger.info('Consumer connected to kafka cluster');
  // subscribe consumer to a topic
  consumer.subscribe(['orders']);
  // start consumer stream
  consumer.consume();
});

producer.on('ready', () => {
  // log successful connection
  logger.info('Producer connected to kafka cluster');
  // start listening for kafka messages
  consumer.on('data', async (message) => {
    // parse order
    const order = JSON.parse(message.value.toString());
    // prepare beverage
    const beverage = await barista.prepare(order);
    // log order to console
    logger.info(`Order ${order.orderId} for ${order.name} is ready`);
    // send message to kafka
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

// start connections
consumer.connect();
producer.connect();
