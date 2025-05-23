import numpy as np
import tensorflow as tf
import os

# 创建一个极简单的模型
model = tf.keras.Sequential([
    tf.keras.layers.Dense(3, activation='softmax', input_shape=(1,))
])

model.compile(optimizer='adam', loss='categorical_crossentropy')

# 生成极简单的训练数据
x = np.array([[1], [5], [10]], dtype=np.float32)
y = np.array([
    [0.8, 0.1, 0.1],  # 低强度 -> 有氧
    [0.1, 0.8, 0.1],  # 中强度 -> 平衡
    [0.1, 0.1, 0.8],  # 高强度 -> 力量
], dtype=np.float32)

# 训练模型
model.fit(x, y, epochs=100, verbose=0)

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

# 测试
test_input = np.array([[3.0]], dtype=np.float32)
interpreter.set_tensor(input_details[0]['index'], test_input)
interpreter.invoke()
output = interpreter.get_tensor(output_details[0]['index'])

print("\nTest prediction for 3 hours of exercise:")
print("Cardio:", output[0][0])
print("Balanced:", output[0][1])
print("Strength:", output[0][2]) 