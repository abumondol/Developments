close all
for s = 3:3
    ai = data(3).annot_indices;
    d = data(s).hw_data;
    xyz = [];
    for i = 1:5
        xyz = [xyz; d(ai(i,1):ai(i,2), 2:4)];
    end
end

[histFreq, histXout] = hist(xyz(:,1), [-1:0.02:1]);
figure;
bar(histXout, histFreq/sum(histFreq)*100, 'BarWidth', 1);
xlabel('g_x');
ylabel('Frequency (percent)');

[histFreq, histXout] = hist(xyz(:,2), [-1:0.02:1]);
figure;
bar(histXout, histFreq/sum(histFreq)*100, 'BarWidth', 1);
xlabel('g_y');
ylabel('Frequency (percent)');

[histFreq, histXout] = hist(xyz(:,3), [-1:0.02:1]);
figure;
bar(histXout, histFreq/sum(histFreq)*100, 'BarWidth', 1);
xlabel('g_z');
ylabel('Frequency (percent)');
return

figure
hist(xyz(:,1), [-1:0.01:1]);
xlabel('g_x');
figure;
hist(xyz(:,2), [-1:0.01:1]);
xlabel('g_y');
figure
hist(xyz(:,3), [-1:0.01:1]);
xlabel('g_z');
return

c = pca(xyz)
xyz = xyz *c;
figure
hist(xyz(:,1), [-1:0.01:1]);
figure;
hist(xyz(:,2), [-1:0.01:1]);
figure
hist(xyz(:,3), [-1:0.01:1]);