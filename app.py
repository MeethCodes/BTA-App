from flask import Flask, request, jsonify
import pandas as pd
import tensorflow as tf
import pickle

app = Flask(__name__)

# Load the trained model
with open('BTA_model.pkl', 'rb') as file:
    model = pickle.load(file)

@app.route('/predict', methods=['POST'])
def predict():
    # Get the input data from the request
    input_data = request.json
    
    # Parse the JSON object and extract the input values
    input_values = [input_data.get(feature) for feature in ['HighBP', 'HighChol', 'BMI', 'Stroke', 
                                                           'HeartDiseaseorAttack', 'PhysActivity',
                                                           'HvyAlcoholConsump', 'GenHlth', 'DiffWalk', 'Age']]
    
    # Create a DataFrame with the input values
    input_df = pd.DataFrame([input_values], columns=['HighBP', 'HighChol', 'BMI', 'Stroke', 
                                                     'HeartDiseaseorAttack', 'PhysActivity',
                                                     'HvyAlcoholConsump', 'GenHlth', 'DiffWalk', 'Age'])
    
    # Convert the input data into the appropriate format for prediction
    input_tensor = tf.convert_to_tensor(input_df.values.astype('float32'), dtype=tf.float32)
    
    # Make predictions
    predictions = model.predict(input_tensor)
    
    # Convert predictions to a list
    predictions_list = predictions.tolist()
    
    # Return the predictions as JSON
    return jsonify(predictions=predictions_list)

if __name__ == '__main__':
    app.run(debug=True)
