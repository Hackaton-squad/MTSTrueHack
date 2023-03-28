#!/bin/bash
mkdir checkpoints
chunk1_download_url="https://huggingface.co/Salesforce/blip2-opt-2.7b/resolve/main/pytorch_model-00001-of-00002.bin"
chunk2_download_url="https://huggingface.co/Salesforce/blip2-opt-2.7b/resolve/main/pytorch_model-00002-of-00002.bin"
index_download_url="https://huggingface.co/Salesforce/blip2-opt-2.7b/resolve/main/pytorch_model.bin.index.json"
config_download_url="https://huggingface.co/Salesforce/blip2-opt-2.7b/resolve/main/config.json"
wget ${chunk1_download_url} -O "checkpoints/pytorch_model-00001-of-00002.bin"
wget ${chunk2_download_url} -O "checkpoints/pytorch_model-00002-of-00002.bin"
wget ${index_download_url} -O "checkpoints/pytorch_model.bin.index.json"
wget ${config_download_url} -O "checkpoints/config.json"