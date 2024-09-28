import logging

logging.basicConfig(
    format=u'%(filename)s [LINE:%(lineno)d] #%(levelname)-8s [%(asctime)s]  %(message)s',
    level=logging.INFO,
    filename="../src/main/resources/pythonApi/pylog.log"
)
logger = logging.getLogger()
logger.setLevel(logging.INFO)
