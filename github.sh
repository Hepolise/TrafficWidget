#!/bin/bash
if [[ -z "$1" ]]; then
  echo "Usage: ${0##*/} 'commit's name'"
fi
cd /home/hepolise/development/Widget/
git add .
git commit -m "$1"
git push -u origin master
