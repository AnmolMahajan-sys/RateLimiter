import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '15s',
};

export default function() {
    const res = http.get('http://localhost:8080/hello');

    check(res, {
    'status is 200': (r) => r.status ===200,
    'status is 429': (r) => r.status ===429,
    });

    sleep(0,1);
}