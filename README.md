

## 使用的知识点

android:descendantFocusability用法:

> 该属性是当一个为view获取焦点时，定义viewGroup和其子控件两者之间的关系。

**属性的值有三种：**

- beforeDescendants：viewgroup会优先其子类控件而获取到焦点

- afterDescendants：viewgroup只有当其子类控件不需要获取焦点时才获取焦点

- blocksDescendants：viewgroup会覆盖子类控件而直接获得焦点

