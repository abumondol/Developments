%load('C:\ASM\DevData\realdisp\matlab\realdisp_data');

pos = 3;
mag = [];
a = [];
for s = 1:17    
    accel = data(s).ideal.pos(3).accel;    
    m = sum(accel.*accel, 2);
    a = [a; data(s).ideal.activity];
    
    l1 = length(m);    
    m = m(m>0);
    l2 = length(m);
    fprintf('Subject %d: %d, %d\n', s, l1, l2);    
    
    mag = [mag; m];
end

mag = abs(mag-95.25);
count = length(mag)

m = [];
for i=1:500:count-500
    if max(mag(i:i+499))<50
        m = [m; mag(i:i+499)];
    end
end

size(m)
return

%mag = mag(a==1);
mag = mag(mag<=100);
length(mag)
figure
hist(mag, 1000)

save
return

figure
len = 3000
for i=1:len:count
    plot(mag(i:i+len))
    ylim([-10, 1500]);
    waitforbuttonpress
end