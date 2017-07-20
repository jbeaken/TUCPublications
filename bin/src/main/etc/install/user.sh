#!/bin/bash
groupadd -g 220 tomcat
useradd -u 220 -g tomcat -r -s /bin/false tomcat
