#!/bin/bash
DIR="$(echo -n $( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd ))"
oc create -f $DIR/jenkins-slave-setup.yml
oc new-app jenkins-ephemeral
oc set resources dc/jenkins --limits=cpu=2,memory=2Gi --requests=cpu=100m,memory=512Mi 
oc label dc jenkins app=jenkins --overwrite
oc create -f $DIR/jenkins-pipeline.yml