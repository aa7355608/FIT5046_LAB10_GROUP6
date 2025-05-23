import numpy as np
import tensorflow as tf
import os

# 创建一个更简单的模型
model = tf.keras.Sequential([
    tf.keras.layers.Dense(4, activation='relu', input_shape=(1,)),
    tf.keras.layers.Dense(3, activation='softmax')
])

model.compile(optimizer='adam', loss='categorical_crossentropy')

# 生成简单的训练数据
hours = np.linspace(0, 15, 100).reshape(-1, 1)
labels = []

for h in hours:
    if h[0] < 5:
        labels.append([0.8, 0.1, 0.1])  # 倾向于有氧
    elif h[0] < 10:
        labels.append([0.1, 0.8, 0.1])  # 倾向于平衡
    else:
        labels.append([0.1, 0.1, 0.8])  # 倾向于力量

labels = np.array(labels)

# 训练模型
model.fit(hours, labels, epochs=10, verbose=1)

# 转换为 TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# 确保目录存在
os.makedirs('app/src/main/assets', exist_ok=True)

# 保存模型
with open('app/src/main/assets/workout_model.tflite', 'wb') as f:
    f.write(tflite_model)

print("Model saved successfully!")

# 测试模型
interpreter = tf.lite.Interpreter(model_content=tflite_model)
interpreter.allocate_tensors()

input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# 测试几个输入值
test_hours = [3.0, 7.0, 12.0]
for hour in test_hours:
    input_data = np.array([[hour]], dtype=np.float32)
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()
    output_data = interpreter.get_tensor(output_details[0]['index'])
    print(f"\nTest with {hour} hours:")
    print("Cardio:", output_data[0][0])
    print("Balanced:", output_data[0][1])
    print("Strength:", output_data[0][2]) 