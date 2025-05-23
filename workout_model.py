import tensorflow as tf
import numpy as np

# 创建一个简单的模型
model = tf.keras.Sequential([
    tf.keras.layers.Dense(8, activation='relu', input_shape=(1,)),
    tf.keras.layers.Dense(3, activation='softmax')
])

# 编译模型
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

# 生成训练数据
X = np.linspace(0, 15, 100).reshape(-1, 1)  # 0-15小时的训练数据
y = []

# 生成标签：[有氧运动概率, 平衡训练概率, 力量训练概率]
for hours in X:
    if hours[0] < 5:
        y.append([0.7, 0.2, 0.1])  # 低强度 -> 偏向有氧
    elif hours[0] < 10:
        y.append([0.2, 0.6, 0.2])  # 中等强度 -> 偏向平衡
    else:
        y.append([0.1, 0.2, 0.7])  # 高强度 -> 偏向力量

y = np.array(y)

# 训练模型
model.fit(X, y, epochs=50, verbose=0)

# 转换为 TFLite 格式
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# 保存模型
with open('app/src/main/assets/workout_model.tflite', 'wb') as f:
    f.write(tflite_model)

print("模型已保存到 app/src/main/assets/workout_model.tflite") 