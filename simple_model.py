import numpy as np
from sklearn.neural_network import MLPRegressor
import struct

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
model = MLPRegressor(hidden_layer_sizes=(8,), activation='relu', max_iter=1000)
model.fit(X, y)

# 创建一个简单的二进制格式
# 格式：
# - 4字节：权重数量
# - 接下来是权重值（每个都是4字节浮点数）
weights = []
for layer in model.coefs_:
    weights.extend(layer.flatten())
for bias in model.intercepts_:
    weights.extend(bias.flatten())

# 确保目录存在
import os
os.makedirs('app/src/main/assets', exist_ok=True)

# 保存为简单的二进制格式
with open('app/src/main/assets/workout_model.tflite', 'wb') as f:
    # 写入权重数量
    f.write(struct.pack('i', len(weights)))
    # 写入权重
    for w in weights:
        f.write(struct.pack('f', w))

print("模型已保存到 app/src/main/assets/workout_model.tflite") 