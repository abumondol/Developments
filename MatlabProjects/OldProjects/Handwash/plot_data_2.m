ttls = {'IR Sensor', 'Button'};
sess=1;
res1 = res(sess).res1;
res2 = res(sess).res2;
events = res(sess).events;
count = length(events)

%t = 100:250;
t = 250:count;
%figure
figure('units','normalized','outerposition',[0 0 1 1]);

subplot(3,1,1);
plot(t, res1(t, 3:3), '--');
hold on
plot(t, res2(t, 3:3));
legend('std1','std2','rms1','X2','std2','rms2');

% scatter(t, res1(t, 5), 'x');
% scatter(t, res2(t, 5), 'd');
% scatter(t, events(t, :), '*');

subplot(3,1,2);
scatter(t, res1(t, 4), 'x');
hold on
scatter(t, res2(t, 4), 'd');
legend('RSSI-1','RSSI-2');

 subplot(3,1,3);
 scatter(t, res1(t, 5), 'x');
 hold on
 scatter(t, res2(t, 5), 'd');
 scatter(t, events(t, :), '*');
legend('Watch 1', 'Watch 2', 'laptop');

title(ttls(sess));
xlabel('time(sec)');

