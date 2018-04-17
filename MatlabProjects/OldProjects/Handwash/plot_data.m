sess=2;
st = data(sess).subject(1).accel(1,1);
et = data(sess).subject(1).accel(end,1);

count = length(st:1000:et);
res1 = zeros(count, 3);
res2 = zeros(count, 3);
events = zeros(count, 1);

a1 = data(sess).subject(1).accel;
a2 = data(sess).subject(2).accel;
b1 = data(sess).subject(1).beacon;
b2 = data(sess).subject(2).beacon;
e = data(sess).events(:,1);
e = (e-st)/1000;
% ad1 = a1(2:end, 2:4) - a1(1:end-1, 2:4);
% ad2 = a2(2:end, 2:4) - a2(1:end-1, 2:4);
% ad1 = [ad1(1,:); ad1];
% ad2 = [ad2(1,:); ad2];
% a1 = [a1, ad1];
% a2 = [a2, ad2];


a1(:,1) = (a1(:,1)-st)/1000;
a2(:,1) = (a2(:,1)-st)/1000;
b1(:,1) = (b1(:,1)-st)/1000;
b2(:,1) = (b2(:,1)-st)/1000;

a1(:,2:4) = mysmooth(a1(:,2:4), 0.8);
a2(:,2:4) = mysmooth(a2(:,2:4), 0.8);

figure
plot(a1(:,1), a1(:,2:4));
hold on
plot(a2(:,1), a2(:,2:4), '--');
plot(b1(:,1), b1(:,3));
plot(b2(:,1), b2(:,3),'--');
legend('X1','Y1','Z1','X2','Y2','Z2','RSSI-1','RSSI-2');

return
for i=1:length(e)
    plot([e(i);e(i)], [10,-10]);
end


