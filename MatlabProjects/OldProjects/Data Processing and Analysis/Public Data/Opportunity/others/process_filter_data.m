Fs = 30;
nyquist_freq = Fs/2;

%%%%% Chebishev high pass Filter %%%%%%%%%%%%%%
Wp = 0.2/nyquist_freq;
Ws = 0.1/nyquist_freq;;
Rp = 1;
Rs = 60;
[n,Wp] = cheb1ord(Wp,Ws,Rp,Rs); 
[b_cheb, a_cheb] = cheby1(n, Rp, Wp, 'high');
%freqz(b_cheb, a_cheb);

%%%%% Equiripple FIR low pass filter Filter %%%%%%%%%%%%%%
f = [0, 1/nyquist_freq, 5/nyquist_freq, 1];
a = [1.0, 1.0, 0, 0];
w = [1, 1];
b_fir = firpm(10,f,a, w);
%freqz(b1,1);

sub_count = length(opp_data);
for sub = 1:sub_count
    sess_count = length(opp_data(sub).session);
    for sess = 1:sess_count        
        pos_count = length(opp_data(sub).session(sess).position);
        for pos = 1:pos_count
            fprintf('Processing subject %d, session %d, pos %d\n', sub, sess, pos);
            d = opp_data(sub).session(sess).position(pos).data(:, 1:3);
            d_low = filter(b_fir, 1, d);            
            d_band = filter(b_cheb, a_cheb, d);            
            
            data(sub).session(sess).position(pos).low = d_low; 
            data(sub).session(sess).position(pos).band = d_band;            
        end                    
    end    
end

save('data', 'data');