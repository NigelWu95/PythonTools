#!/usr/bin/python
# -*- coding:utf-8 -*-

import os
import re
import json
import sys

class JavaFileProcessor:
    """批量处理Java代码文件注释信息"""
    def __init__(self, projectDir):
        self.projectDir = projectDir

    def scanFile(self):
        #扫码项目文件夹下所有的文件
        targetFilesList = []

        for dirPath, dirNames, fileNames in os.walk(self.projectDir):
            for fileName in fileNames:
                if fileName.endswith(".properties"):
                    targetFilesList.append(os.path.join(dirPath, fileName))
        
        return targetFilesList

    # def processFileContent(self, file, originalInfo, newInfo):
    def replaceContent(self, file):
        config = {}
        print file.split(os.sep)[-1]

        with open(file, "r") as fp:
            allLines = fp.readlines()

        properties = []

        for line in allLines:
            if line.startwith("#"):
                pass
            else:
                key = line.split("=")[0]
                value = raw_input(key + "(y/n):")

                if value == "y":
                    line = line[0:line.index("=") + 1] + "\n"

            properties.append(line)

        config['origin'] = allLines
        config['replaces'] = properties

        return config

    # def replaceContent(self, fileContent, targetTemplate):
    #     template = json.loads(json.dumps(targetTemplate))
    #     anotations = fileContent['anotations']

    #     for index in range(len(anotations)):
    #         line = anotations[index]

    #         if "*" not in line:
    #             continue

    #         for key in template.keys():
    #             if key in line:
    #                 anotations[index] = key + template[key]

    #     wholeContent = anotations + fileContent['codes']

    #     return wholeContent

    # def templateToJson(self, targetTemplateFile):
    #     templateJson = {}

    #     with open(targetTemplateFile, "r") as fp:
    #         allLines = fp.readlines()
    #     for line in allLines:
    #         if ":" in line:
    #             key = line[0:line.index(":") + 2]
    #             value = line[line.index(":") + 2:]
    #             templateJson[key] = value
    #         elif " " in line[3:]:
    #             key = line[0:line[3:].index(" ") + 4]
    #             value = line[line[3:].index(" ") + 4:]
    #             templateJson[key] = value
    #         else:
    #             key = line
    #             value = ""
    #             templateJson[key] = value

    #     return templateJson

    def rewriteFile(self):
        for file in self.scanFile():
            replaceContent = self.replaceContent(file)

            with open(file, "w+") as fp:
                fp.writelines(replaceContent)

    def gitBashProcess(self):
        count = 0
        gitCommand = raw_input("Please input git command:\n" + 
            "1. git add .\n2. git commit\n3. git push\n" + 
            "And in the end, please input '\q' to complete and quit.\n")

        while gitCommand != "\q" and gitCommand != "":
            if not gitCommand.startswith("git"):
                gitCommand = raw_input("Please input git command.\n")
                continue
            
            if count == 0 and gitCommand == "git add .":
                count += 1
                gitCommand = raw_input("2. git commit : \n")
            elif count == 0 and gitCommand != "git add .":
                gitCommand = raw_input("1. git add . : \n")
            elif count == 1 and gitCommand == "git commit":
                count += 1
                gitCommand = raw_input("3. git push : \n")
            elif count == 1 and gitCommand != "git commit":
                gitCommand = raw_input("2. git commit : \n")
            elif count == 2 and gitCommand == "git push":
                count += 1
                gitCommand = raw_input("Please input '\q'\n")
            elif count == 2 and gitCommand != "git push":
                gitCommand = raw_input("3. git push : \n")
            else:
                gitCommand = raw_input("Please input git command or '\q'.\n")

            os.popen(gitCommand).read()

if __name__ == '__main__':
    processor = JavaFileProcessor("demo")
    # processor = JavaFileProcessor(sys.argv[1], sys.argv[2], sys.argv[3])
    processor.gitBashProcess()