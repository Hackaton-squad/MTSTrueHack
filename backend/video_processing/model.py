import torch
from transformers import Blip2Processor, Blip2ForConditionalGeneration

PROMPT = ""

class Model:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

        self.processor = Blip2Processor.from_pretrained("Salesforce/blip2-opt-2.7b")
        self.model = Blip2ForConditionalGeneration.from_pretrained("Salesforce/blip2-opt-2.7b")
        self.model.to(self.device)

        for param in self.model.parameters():
            param.requires_grad = False

        self.model.eval()

    def predict_caption(self, image, start_text=""):
        inputs = self.processor(image, text=start_text,
                                return_tensors="pt").to(self.device)
        out = self.model.generate(**inputs, num_beams=5, early_stopping=True,)

        return self.processor.batch_decode(out, skip_special_tokens=True)
