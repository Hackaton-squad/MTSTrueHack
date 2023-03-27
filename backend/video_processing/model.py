import requests
from PIL import Image
from transformers import BlipProcessor, BlipForConditionalGeneration
from transformers import VisionEncoderDecoderModel, ViTImageProcessor, AutoTokenizer
import torch

PROMPT = "the scene from the film in which"


class Model:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

        self.processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-large")
        self.model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-large").to("cuda")
        self.model.to(self.device)

    def predict_caption(self, image, start_text=PROMPT):
        inputs = self.processor(image, text=start_text, return_tensors="pt").to(self.device)
        out = self.model.generate(**inputs)

        return self.processor.batch_decode(out, skip_special_tokens=True)
