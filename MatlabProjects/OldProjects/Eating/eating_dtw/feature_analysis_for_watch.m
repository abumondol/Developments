f = [];
for s = 1:36
    accel = data(s).accel;
    mp = [segments(s).min_points, segments(s).labels(:,1)];
    mp = mp(mp(:,2)>0 & mp(:,2)<400, 1);
    
    for j=1:length(mp)
        ix = mp(j);
        xyz = accel(ix, 2:4);
        a = accel(ix-47:ix+32, 2:4);
        v = var(a);
        f = [f; xyz, v];
    end
    
end

f = f(:, 4:6);
f = sum(f, 2);
f = sort(f)
hist(f, 1000);
return
for i=1:6
    figure
    hist(f(:,i));
end
