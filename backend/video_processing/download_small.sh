#!/bin/bash
mkdir small_checkpoints
model_download_url="https://huggingface.co/Salesforce/blip-image-captioning-large/resolve/main/pytorch_model.bin"
config_download_url="https://huggingface.co/Salesforce/blip-image-captioning-large/resolve/main/config.json"
wget ${model_download_url} -O "small_checkpoints/pytorch_model.bin"
wget ${config_download_url} -O "small_checkpoints/config.json"