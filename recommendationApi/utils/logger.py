import logging

logging.basicConfig(
    format=u'%(filename)s [LINE:%(lineno)d] #%(levelname)-8s [%(asctime)s]  %(message)s',
    level=logging.DEBUG,
    filename="pylog.log"
)
logger = logging.getLogger()
logger.setLevel(logging.DEBUG)
