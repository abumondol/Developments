close all
for s = 3:3
    ai = data(3).annot_indices;
    d = data(s).hw_data;
    xyz = [];
    for i = 1:5
        xyz = [xyz; d(ai(i,1):ai(i,2), 2:4)];
    end
end
figure
hist(xyz(:,1), [-1:0.01:1]);
xlabel('g_x');
figure;
hist(xyz(:,2), [-1:0.01:1]);
xlabel('g_y');
figure
hist(xyz(:,1), [-1:0.01:1]);
xlabel('g_z');

return
c = pca(xyz)
xyz = xyz *c;
figure
hist(xyz(:,1), [-1:0.01:1]);
figure;
hist(xyz(:,2), [-1:0.01:1]);
figure
hist(xyz(:,1), [-1:0.01:1]);