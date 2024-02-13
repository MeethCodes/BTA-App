from flask import Flask, request, jsonify
import pandas as pd
import tensorflow as tf

# Load the trained model using TensorFlow's model loading function
model = tf.keras.models.load_model("my_BTA_model")

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Parse JSON request data and extract input values
        data = request.json
        input_data = pd.DataFrame(data, index=[0])

        # Make predictions
        predictions = model.predict(input_data)

        # Extract the class with the highest probability
        predicted_class = tf.argmax(predictions, axis=1).numpy()[0]

        # Convert class index to label
        labels = ["Non Diabetic", "Pre-Diabetic", "Diabetic"]
        predicted_label = labels[predicted_class]

        # Optionally, return probability as well
        probability = predictions[0][predicted_class] * 100
        response = {"prediction": predicted_label, "probability": f"{probability:.2f}%"}

        return jsonify(response), 200
    except Exception as e:
        # Log and return error response
        app.logger.error(f"Prediction error: {str(e)}")
        return jsonify({"error": "Prediction failed"}), 500

if __name__ == '__main__':
    app.run(debug=True)
