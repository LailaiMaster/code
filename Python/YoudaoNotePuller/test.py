import os


def set_relative_file_path(local_dir, file_path, file_name, file_dirname):
    """ 图片/附件设置为相对地址 """

    relative_path = file_path.replace(local_dir, '')
    print('relative_path: %s', relative_path)
    layer_count = len(relative_path.split('/'))
    if layer_count == 2:
        new_file_path = os.path.join('./', file_dirname, file_name).replace('\\', '/')
        return new_file_path
    relative = ''
    if layer_count > 2:
        sub_count = layer_count - 2
        for i in range(sub_count):
            relative = os.path.join(relative, '../')
    new_file_path = os.path.join(relative, file_dirname, file_name).replace('\\', '/')
    return new_file_path


def main():
    print("current", "D:\dev_data\youdaonote-pull",)
    return
