a = csvread('features/subject_1.csv');
a = a(11:end, end);
b = csvread('LSTM/res_1.csv');
count = length(a)

for i = 1:50:count
    close all
    figure
    plot(a(i:i+99))
    hold on
    plot(b(i:i+99))
    ylim([0, 1]);
    waitforbuttonpress
end