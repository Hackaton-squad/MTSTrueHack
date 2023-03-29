import torch
from transformers import BlipProcessor, BlipForConditionalGeneration

PROMPT = "the scene from the film in which"

class Model:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

        self.processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-large")
        self.model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-large")
        self.model.to(self.device)

        for param in self.model.parameters():
            param.requires_grad = False

        self.model.eval()

    def predict_caption(self, image, start_text=PROMPT):
        inputs = self.processor(image, text=start_text,
                                return_tensors="pt").to(self.device)
        out = self.model.generate(**inputs, early_stopping=True)

        return self.processor.batch_decode(out, skip_special_tokens=True)
