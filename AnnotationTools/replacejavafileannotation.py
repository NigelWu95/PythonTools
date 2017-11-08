#!/usr/bin/python
# -*- coding:utf-8 -*-

import os
import re
import json
import sys

class JavaFileProcessor:
    """批量处理Java代码文件注释信息"""
    def __init__(self, projectDir, suffix, targetTemplateFile):
        self.projectDir = projectDir
        self.suffix = suffix
        self.targetTemplateFile = targetTemplateFile

    def scanFile(self):
        #扫码项目文件夹下所有的文件
        targetFilesList = []

        for dirPath, dirNames, fileNames in os.walk(self.projectDir):
            for fileName in fileNames:
                if fileName.endswith(self.suffix):
                    targetFilesList.append(os.path.join(dirPath, fileName))
        
        return targetFilesList

    # def processFileContent(self, file, originalInfo, newInfo):
    def getFileContent(self, file):
        with open(file, "r") as fp:
            allLines = fp.readlines()

        flag = False
        count = 0
        anotations = []
        codes = []

        for line in allLines:
            if "/**" in line:
                flag = True

            if "*/" in line:
                flag = False
                count += 1

            if flag or count == 1:
                # print line
                anotations.append(line)
            else:
                codes.append(line)

        fileContent = {}
        fileContent['anotations'] = anotations
        fileContent['codes'] = codes

        return fileContent

    def replaceContent(self, fileContent, targetTemplate):
        template = json.loads(json.dumps(targetTemplate))
        anotations = fileContent['anotations']

        for index in range(len(anotations)):
            line = anotations[index]

            if "*" not in line:
                continue

            for key in template.keys():
                if key in line:
                    anotations[index] = key + template[key]

        wholeContent = anotations + fileContent['codes']

        return wholeContent

    def templateToJson(self, targetTemplateFile):
        templateJson = {}

        with open(targetTemplateFile, "r") as fp:
            allLines = fp.readlines()
        for line in allLines:
            if ":" in line:
                key = line[0:line.index(":") + 2]
                value = line[line.index(":") + 2:]
                templateJson[key] = value
            elif " " in line[3:]:
                key = line[0:line[3:].index(" ") + 4]
                value = line[line[3:].index(" ") + 4:]
                templateJson[key] = value
            else:
                key = line
                value = ""
                templateJson[key] = value

        return templateJson

    def rewriteFile(self):
        targetTemplate = self.templateToJson(self.targetTemplateFile)

        for file in self.scanFile():
            fileContent = self.getFileContent(file)
            finalContent = self.replaceContent(fileContent, targetTemplate)
            with open(file, "w+") as fp:
                fp.writelines(finalContent)

if __name__ == '__main__':
    # processor = JavaFileProcessor("./sdkdemo", ".java", "./t.java")
    processor = JavaFileProcessor(sys.argv[1], sys.argv[2], sys.argv[3])
    processor.rewriteFile()