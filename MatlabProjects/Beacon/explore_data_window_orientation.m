srcFolder = 'C:\Users\mm5gg\Box Sync\MyData\Beacon\Beacon data Sep 28 2017 Thu\';
d = csvread(strcat(srcFolder,'orientation_nexus.csv'));

d(:,2) = d(:, 2) - d(1,2);
d(:,2) = d(:,2)/1000;
log = d(d(:,end)==0, 1:2);
b = d(d(:,end)~=0, :);
b(:,3) = b(:,3) + 80;

count = round(log(end,2));
b13 = zeros(count, 3);
b14 = zeros(count, 3);
b15 = zeros(count, 3);

for t=1:count
    bw = b(b(:,2)>=t &  b(:,2)<t+1, :);    
    rssi = bw(bw(:, 1)==13, 3);
    if ~isempty(rssi)
        b13(t, :) = [t, mean(rssi), length(rssi)];
    else
        b13(t, :) = [t, 0, 0];
    end
    
    rssi = bw(bw(:, 1)==14, 3);
    if ~isempty(rssi)
        b14(t, :) = [t, mean(rssi), length(rssi)];
    else
        b14(t, :) = [t, 0, 0];
    end
    
    rssi = bw(bw(:, 1)==15, 3);
    if ~isempty(rssi)
        b15(t, :) = [t, mean(rssi), length(rssi)];
    else
        b15(t, :) = [t, 0, 0];
    end
    
end

b13 = b13(b13(:,3)>0, :);
b14 = b14(b14(:,3)>0, :);
b15 = b15(b15(:,3)>0, :);


figure
scatter(b13(:,1), b13(:,2),'or');
hold on
scatter(b14(:,1), b14(:,2),'xg');
scatter(b15(:,1), b15(:,2),'+b');

scatter(b13(:,1), b13(:,3),'or');
scatter(b14(:,1), b14(:,3),'xg');
scatter(b15(:,1), b15(:,3),'+b');

legend('Nexus-B13', 'Nexus-B14', 'Nexus-B15', 'NexusCount-B13', 'NexusCount-B14', 'NexusCount-B15');
title('Orientation');

log_odd = log(2:2:end, :);
log_even = log(1:2:end, :);
scatter(log_odd(:, 2), zeros(length(log_odd), 1), '*g');
scatter(log_even(:, 2), zeros(length(log_even), 1), '*r');

plot([0; log(end,2)], [0;0], 'k');
for i = 1:length(log_odd)
    plot([log_odd(i,2);log_odd(i,2)], [15, -50], '--g');
end

for i = 1:length(log_even)
    plot([log_even(i,2);log_even(i,2)], [15, -50], ':r');
end
