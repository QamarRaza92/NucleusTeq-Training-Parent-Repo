def convert_to_error_messages(arg):
    return [err['msg'] for err in arg['detail']]