#!/usr/bin/python
# -*- coding:utf-8 -*-

import os
import re
import json
import sys

class JavaFileProcessor:
    def __init__(self, projectDir):
        self.projectDir = projectDir

    def scanFile(self):
        targetFilesList = []

        for dirPath, dirNames, fileNames in os.walk(self.projectDir):
            for fileName in fileNames:
                if fileName.endswith(".properties"):
                    targetFilesList.append(os.path.join(dirPath, fileName))
        
        return targetFilesList

    def configContent(self, file):
        config = {}
        print file.split(os.sep)[-1]

        with open(file, "r") as fp:
            allLines = fp.readlines()

        properties = []

        for line in allLines:
            if line.startswith("#"):
                pass
            else:
                key = line.split("=")[0]
                value = raw_input(key + "(y/n):")

                if value == "y":
                    line = line[0:line.index("=") + 1] + "\n"

            properties.append(line)

        config['origin'] = allLines
        config['replace'] = properties

        return config

    def rewriteFile(self):
        restoreContent = {}

        for file in self.scanFile():
            configContent = self.configContent(file)
            print "OK"
            restoreContent[file] = configContent['origin']
            replaceContent = configContent['replace']

            with open(file, "w+") as fp:
                fp.writelines(replaceContent)

        self.gitBashProcess()

        for key in restoreContent.keys():
            with open(key, "w+") as fp:
                fp.writelines(restoreContent[key])

    def gitBashProcess(self):
        count = 0
        gitCommand = raw_input("Please input git command:\n" + 
            "1. git add ./<filePath>\n2. git commit -m \"***\"\n3. git push\n" + 
            "And in the end, please input '\q' to complete and quit.\n")

        while gitCommand != "\q" and gitCommand != "":
            if not gitCommand.startswith("git"):
                gitCommand = raw_input("Please input git command.\n")
                continue
            
            if count == 0 and gitCommand.startswith("git add "):
                os.popen(gitCommand).read()
                count += 1
                gitCommand = raw_input("2. git commit -m \"***\" : \n")
            elif count == 0 and not gitCommand.startswith("git add "):
                gitCommand = raw_input("1. git add ./<filePath> : \n")
            elif count == 1 and gitCommand.startswith("git commit -m"):
                os.popen(gitCommand).read()
                count += 1
                gitCommand = raw_input("3. git push : \n")
            elif count == 1 and not gitCommand.startswith("git commit -m"):
                gitCommand = raw_input("2. git commit -m \"***\" : \n")
            elif count == 2 and gitCommand == "git push":
                os.popen(gitCommand).read()
                count += 1
                gitCommand = raw_input("Please input '\q'\n")
            elif count == 2 and gitCommand != "git push":
                gitCommand = raw_input("3. git push : \n")
            else:
                gitCommand = raw_input("Please input git command or '\q'.\n")

if __name__ == '__main__':
    # 该参数表示要上传的代码路径名
    # processor = JavaFileProcessor("demo")
    processor = JavaFileProcessor(sys.argv[1])
    processor.rewriteFile()