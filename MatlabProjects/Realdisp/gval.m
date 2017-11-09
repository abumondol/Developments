function g = gval(mag)
    count = length(mag);
    len = 50;
    d = m(1:len);
    minavg = mean(d);
    minvar = var(d);
    for i = 1:len:count
        d = m(1:len);
        v = var(d);
        if v<minvar
            minvar = v;
            minavg = mean(d);
        end
    end

    g = minavg;

end