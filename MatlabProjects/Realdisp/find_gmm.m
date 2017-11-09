%load('C:\ASM\DevData\realdisp\matlab\realdisp_data');

pos = 3;
mag = [];
a = [];
for s = 1:17    
    accel = data(s).ideal.pos(3).accel;    
    m = sum(accel.*accel, 2);        
    mag = [mag; m];
    a = [a; accel];
end

mag = abs(mag-95.25);
count = length(mag)
m = [];
d = [];
for i=1:500:count-500
    if max(mag(i:i+499))<50
        %m = [m; mag(i:i+499)];
        d = [d; a(i:i+499, :)];
    end
end
size(d)

options = statset('MaxIter', 1000);
GMModels = {};
aics =[];
bics = [];
for k=2:15
    GMModels{k} = fitgmdist(d, k, 'Options',options, 'CovarianceType','diagonal');
    aics = [aics; k, GMModels{k}.AIC]    
    bics = [bics; k, GMModels{k}.BIC]    
end

figure
plot(aics(:,1), aics(:,2))

save('GMModels2', 'GMModels')
