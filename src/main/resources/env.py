from configparser import ConfigParser
import configparser


def read_postgres_properties_from_file(file_path: str):
    db_section = "POSTGRESQL"
    config: ConfigParser = configparser.ConfigParser()
    config.read(file_path)

    properties: dict[str, str] = {}
    for section in config.items(db_section):
        key, value = section
        properties[key] = value

    return properties
